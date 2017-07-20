package mjr.techtest.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ModificationRecord {

	HashMap<String, LinkedList<ModificationRecordItem>> modifications;
	
	
	public ModificationRecord() {
		modifications = new HashMap<String, LinkedList<ModificationRecordItem>>();
	}
	
	
	public void recordModification(String itemType, ModificationRecordItem modification) {
		LinkedList<ModificationRecordItem> modificationsForType = modifications.get(itemType);
		
		if (null == modificationsForType) {
			modificationsForType = new LinkedList<ModificationRecordItem>();
		}
		else {
			modifications.remove(modificationsForType);
		}
		modificationsForType.add(modification);
		modifications.put(itemType, modificationsForType);		
	}
	
	
	public Set<String> getModifiedTypes() {
		return modifications.keySet();
	}
	
	public List<ModificationRecordItem> getModificationsForType(String itemType) {
		return modifications.get(itemType);
	}

}
