package org.metaborg.java.conformance.util;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermVisitor;

import com.google.common.base.Predicate;

public class TermTools {
	public static boolean isList(IStrategoTerm term) {
		return term.getTermType() == IStrategoTerm.LIST;
	}
	
	public static boolean isAppl(IStrategoTerm term, String name) {
		if(term.getTermType() == IStrategoTerm.APPL) {
			final IStrategoAppl appl = (IStrategoAppl) term;
			if(appl.getConstructor().getName().equals(name))
				return true;
		}
		return false;
	}

	public static boolean isAppl(IStrategoTerm term, String name, int arity) {
		if(term.getTermType() == IStrategoTerm.APPL) {
			final IStrategoAppl appl = (IStrategoAppl) term;
			final IStrategoConstructor cons = appl.getConstructor();
			if(cons.getName().equals(name) && cons.getArity() == arity)
				return true;
		}
		return false;
	}


	public static IStrategoTerm uriSegments(IStrategoTerm uri) {
		return uri.getSubterm(1);
	}

	public static IStrategoTerm uriHeadSegment(IStrategoTerm uri) {
		return uriSegments(uri).getSubterm(0);
	}

	public static IStrategoAppl uriNamespace(IStrategoTerm uri) {
		return (IStrategoAppl) uriHeadSegment(uri).getSubterm(0);
	}

	public static String uriName(IStrategoTerm uri) {
		return ((IStrategoString) uriHeadSegment(uri).getSubterm(1)).stringValue();
	}


	public static IStrategoTerm collectOne(IStrategoTerm term, Predicate<IStrategoTerm> pred) {
		final CollectOneVisitor visitor = new CollectOneVisitor(pred);
		visitor.visit(term);
		return visitor.result();
	}

	public static IStrategoTerm collectUseTask(IStrategoTerm term) {
		final CollectOneVisitor visitor = new CollectOneVisitor(new Predicate<IStrategoTerm>() {
			@Override
			public boolean apply(IStrategoTerm input) {
				return isAppl(input, "Use");
			}
		});
		visitor.visit(term);
		return visitor.result().getSubterm(0).getSubterm(0);
	}
}

class CollectOneVisitor extends TermVisitor {
	private final Predicate<IStrategoTerm> pred;

	private IStrategoTerm result;

	public CollectOneVisitor(Predicate<IStrategoTerm> pred) {
		this.pred = pred;
	}

	@Override
	public void preVisit(IStrategoTerm term) {
		for(IStrategoTerm anno : term.getAnnotations()) {
			if(pred.apply(anno)) {
				result = anno;
				break;
			}
		}
		
		if(pred.apply(term)) {
			result = term;
		}
	}

	@Override
	public boolean isDone(IStrategoTerm term) {
		return result != null;
	}

	public IStrategoTerm result() {
		return result;
	}
}
