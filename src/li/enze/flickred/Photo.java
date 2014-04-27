package li.enze.flickred;

import android.util.Log;

public class Photo {
	
	public final String id;
	public final String owner;
	public final String secret;
	public final String server;
	public final String farm;
	public final String title;
	public final String datetaken;
	public final String ownername;
	public final String description;
	
    Photo (String id, String owner, String secret, 
    		String server, String farm, String title,
    		String datetaken, String ownername, String description) {
    	this.id = id;
    	this.owner = owner;
    	this.secret = secret;
    	this.server = server;
    	this.farm = farm;
    	this.title = title;
    	this.datetaken = datetaken;
    	this.ownername = ownername;
    	this.description = description;
    }
    
    // http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg
    public String url_s(){
    	return "http://farm" + this.farm
    		+ ".staticflickr.com/"+ this.server
    		+ "/"+ this.id
    		+ "_"+ this.secret 
    		+ "_s.jpg";
    }
    
    public String url_c(){
    	return "http://farm" + this.farm
    		+ ".staticflickr.com/"+ this.server
    		+ "/"+ this.id
    		+ "_"+ this.secret 
    		+ "_c.jpg";
    }
    
    public void minilog(){
    	Log.i("PHOTO", "Photo id: "+this.id);
    	Log.i("PHOTO", "Title: "+this.title);
    }
    
    public void log(){
    	Log.i("PHOTO", "Photo id: "+this.id);
    	Log.i("PHOTO", "Owner: "+this.owner);
    	Log.i("PHOTO", "Secret: "+this.secret);
    	Log.i("PHOTO", "Server: "+this.server);
    	Log.i("PHOTO", "Farm: "+this.farm);
    	Log.i("PHOTO", "Title: "+this.title);
    	Log.i("PHOTO", "Date Taken: "+this.datetaken);
    	Log.i("PHOTO", "Ownername: "+this.ownername);
    	// TODO: fix null decription
//    	Log.i("PHOTO", "Description: "+this.description.toString());
    }


}
