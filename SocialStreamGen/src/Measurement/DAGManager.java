package Measurement;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import Measurement.Object.DAG;
import Measurement.Object.MItem;

/**
 * @author Chengcheng Yu
 */
public class DAGManager {
	Map<String, MItem> items = new HashMap<String, MItem>();// <itemId,itemInfor>
	Map<String, DAG> dags = new HashMap<String, DAG>();// <DAGId,patents>

	/*
	 * get the dag of the itemId belong to
	 */
	public DAG getDag_itemBelongTo(String itemId) {
		if (items.containsKey(itemId)) {
			String DAG_id = items.get(itemId).getDAG_id();
			if (dags.containsKey(DAG_id)) {
				return dags.get(DAG_id);
			}
		}
		return null;
	}

	public boolean isBelongToDAG(String itemId) {
		if (items.containsKey(itemId)) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * get the dag according to the dagId of the dag
	 */
	public DAG getDag(String dagId) {
		// TODO Auto-generated method stub
		return dags.get(dagId);
	}

	public Map<String, MItem> getItems() {
		return items;
	}

	public void setItems(Map<String, MItem> items) {
		this.items = items;
	}

	public Map<String, DAG> getDags() {
		return dags;
	}

	public void setDags(Map<String, DAG> dags) {
		this.dags = dags;
	}

	/*
	 * get the height of a item according to the itemId of the item
	 */
	public Integer getItemHeight(String itemId) {
		return items.get(itemId).getHeight();
	}

	/*
	 * set the height of a item according to the itemId of the item
	 */
	public void setItemHeight(String itemId, Integer height) {
		items.get(itemId).setHeight(height);
	}

	/*
	 * set the height of a item according to the itemId of the item
	 */
	public void setItemDagId(String itemId, String DAG_id) {
		items.get(itemId).setDAG_id(DAG_id);
	}

	/**
	 * merger the dag of the (itemId) belong to to (idag), and return the (idag)
	 */
	public void merege(String citing, String cited) {
		// TODO Auto-generated method stub
		if (isBelongToDAG(cited)) { // put citing into the dag of cited
			DAG idag = getDag_itemBelongTo(cited);

			int citing_height = getItemHeight(cited) + 1;
			int idag_hieght = idag.getHeight();

			// set idag height
			int idag_height = (citing_height > idag_hieght ? citing_height : idag_hieght);
			idag.setHeight(idag_height);

			if (isBelongToDAG(citing)) {
				// set the citing height
				if (getItemHeight(citing) < citing_height) {
					setItemHeight(citing, citing_height);
				}
			} else {
				//put citing into items
				items.put(citing, new MItem(idag.getId(),citing_height));
				//put citing into idag members
				idag.getMembers().add(citing);
			}

		} else {
			if (isBelongToDAG(citing)) {//put cited into the dag of citing
				DAG idag = getDag_itemBelongTo(citing);
				idag.getMembers().add(cited);
				items.put(cited,new MItem(idag.getId(),0));
			} else {//create a new dag, put citing and cited into the dag
				HashSet<String> members = new HashSet<String>();
				members.add(citing);
				members.add(cited);
				Integer dag_height = 1;
				DAG idag = new DAG(citing,members,dag_height);
				
				addDag(idag);
				items.put(citing,new MItem(idag.getId(),1));
				items.put(cited,new MItem(idag.getId(),0));
			}
		}

	}

	/** 
	 *  
	 */
	public void addDag(DAG dag) {
		// TODO Auto-generated method stub
		dags.put(dag.getId(), dag);
	}

	public void removeDAG(DAG dag) {
		dags.remove(dag.getId());
	}

}
