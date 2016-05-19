package rcptext;

import java.util.ArrayList;

import gnu.trove.TIntProcedure;

public class SaveIDToListProcedure implements TIntProcedure{

	private ArrayList<Integer> ids = new ArrayList<Integer>();
	@Override
	public boolean execute(int id) {
		// TODO Auto-generated method stub
		 ids.add(id);
         return true;
	}

	public ArrayList<Integer> getIds(){
		return ids;
	}
}
