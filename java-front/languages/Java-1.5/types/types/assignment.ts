module languages/Java-1.5/types/types/assignment

imports
	
	include/Java
	lib/task/-
	lib/types/-
	lib/properties/-
	lib/relations/-
	
	languages/Java-1.5/types/types/widening
	languages/Java-1.5/types/types/narrowing
	
type rules

	e-ty <assign-conv: v-ty
	where e-ty == v-ty 
	   or e-ty <widens: v-ty
	   or ( // TODO: only for constant expressions, only if value of e-ty is representable in v-ty
	   	    (e-ty == Byte() or e-ty == Short() or e-ty == Char() or e-ty == Int())
	   	and (v-ty == Byte() or v-ty == Short() or v-ty == Char())
	   	and e <representable: v-ty // TODO: need value of e-ty here
	   	and e-ty <narrow-prim: v-ty
   	)
	
	// pg 397. Compount assignment E1 op= E2 is equivalent to E1 = (T)((E1) op (E2)) where T is type of E1.
	e-ty <comp-assign-conv: v-ty
	where e-ty == v-ty
	   or e-ty <widens-prim:  v-ty
	   or e-ty <narrows-prim: v-ty