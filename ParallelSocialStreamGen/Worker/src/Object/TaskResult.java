package Object;

import java.util.Set;

public class TaskResult{
	String id;
	Set<String> links;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	
	public Set<String> getLinks() {
		return links;
	}

	public void setLinks(Set<String> links) {
		this.links = links;
	}

	@Override
	public String toString() {
		String result = id;
		if(links!=null){
			for(String link:links){
				result+=","+link;
			}
		}
		return result;
	}
}