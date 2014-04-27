package li.enze.flickred;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	private ArrayList<Photo> photos;
	private static LayoutInflater inflater = null;
	
	public ListViewAdapter(Activity activity){
		this.photos = new ArrayList<Photo>();
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void addPhotos(ArrayList<Photo> newPhotos) {
		this.photos.addAll(newPhotos);
		this.notifyDataSetChanged();
	} 
	
	@Override
	public int getCount() {
		return photos.size();
	}

	@Override
	public Photo getItem(int position) {
		return photos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	       Photo photo = getItem(position);
	       View view = convertView;
	       if (view == null) // no view to re-use, create new
	           view = inflater.inflate(R.layout.list_row, null);
	       
	       
	       TextView titleView = (TextView) view.findViewById(R.id.title);
	       TextView ownernameView = (TextView) view.findViewById(R.id.ownername);
	       TextView datetakenView = (TextView) view.findViewById(R.id.datetaken);
	       TextView descriptionView = (TextView) view.findViewById(R.id.description);
	       
	       ImageView thumbnailView = (ImageView) view.findViewById(R.id.list_image);
	       
	       if (photo.title.length() >0){
	    	   titleView.setText(photo.title);
	       } else {
	    	   titleView.setText("Untitled");
	       }
	       
	       ownernameView.setText(photo.ownername);
	       datetakenView.setText(photo.datetaken);
	       descriptionView.setText(photo.description);
	    	       
	       thumbnailView.setImageBitmap(null);
	       
	       UrlImageViewHelper.setUrlDrawable(thumbnailView, photo.url_s());

	       return view;
	}
	
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}


}
