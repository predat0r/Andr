package person.tarasenko.nasarss;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    public boolean refreshed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	//	String url = (String)findViewById(R.string.link);
		final IotdHandler handler = new IotdHandler();
        refreshed = false;

        while (!refreshed){
        new Thread(new Runnable() {
            @Override
            public void run() {
                refreshed = handler.processFeed();

            }
        }).start();}

        resetDisplay(handler.getTitle(), handler.getDate(), handler.getImage(), handler.getDescription());

		System.out.println("All done");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void resetDisplay(String title, String date, Bitmap image, StringBuffer description) {
		TextView titleView = (TextView)findViewById(R.id.textTitle);
		titleView.setText(title);
		
		TextView dateView = (TextView)findViewById(R.id.textDate);
		dateView.setText(date);
		
		ImageView imageView = (ImageView)findViewById(R.id.imageD);		
		imageView.setImageBitmap(image);
		
		TextView descriptionView = (TextView)findViewById(R.id.textDesc);
		descriptionView.setText(description);
		
	}
	
}


