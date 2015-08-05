package com.qualcode.quickmarks;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Browser;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class BookmarkList extends ListActivity {
    protected CustomAdapter adapter;
    private List<BookmarkItem> bookmarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PackageManager pm = getPackageManager();

        final ResolveInfo mInfo = pm.resolveActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")), 0);

        final String packageName = String.valueOf(pm.getApplicationLabel(mInfo.activityInfo.applicationInfo)).toLowerCase();
        //Toast.makeText(this, packageName, Toast.LENGTH_LONG).show();

        if (packageName.contains("firefox"))
        {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(new ComponentName("org.mozilla.firefox", "org.mozilla.firefox.App"));
            intent.setAction("org.mozilla.gecko.BOOKMARK");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //startActivityForResult(intent, 1);
            return;
        }

        setContentView(R.layout.bookmark_list);

        adapter = new CustomAdapter();
        bookmarks = new ArrayList<>();

        String browserURi;


        if(packageName.contains("chrome"))
            browserURi = getResources().getString(R.string.content_uri_chrome);
        else
            browserURi = getResources().getString(R.string.content_uri_default);

        String[] projection = new String[] {Browser.BookmarkColumns.FAVICON, Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL};
        Uri uri = Uri.parse(browserURi);
        final  Cursor cursor = getContentResolver().query(uri, projection, Browser.BookmarkColumns.BOOKMARK + " = 1", null, null);

        if (cursor == null) return;

        if (cursor.moveToFirst())
        {
            while (!cursor.isAfterLast()) {

                BookmarkItem bm = new BookmarkItem();
                bm.setTitle(cursor.getString(cursor.getColumnIndex(Browser.BookmarkColumns.TITLE)));
                bm.setUrl(cursor.getString(cursor.getColumnIndex(Browser.BookmarkColumns.URL)));
                bm.setFavicon(cursor.getBlob(cursor.getColumnIndex(Browser.BookmarkColumns.FAVICON)));

                adapter.addItem(bm);
                bookmarks.add(bm.copy());

                cursor.moveToNext();
            }
        }

        if (cursor.isClosed() == false)
            cursor.close();

        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        final BookmarkItem bookmark = bookmarks.get(position);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bookmark.getUrl()));
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        startActivity(intent);
        //startActivityForResult(intent, 1);
    }

    /* ADAPTER */
    private class CustomAdapter extends BaseAdapter
    {
        private final List<BookmarkItem> bookmarks = new ArrayList<>();
        private final LayoutInflater mInflater;

        public CustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(BookmarkItem bookmark) {
            bookmarks.add(bookmark);
        }

        @Override
        public int getCount() {
            return bookmarks.size();
        }

        @Override
        public Object getItem(int position) {
            return bookmarks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent)
        {
            final ViewHolder holder;
            final BookmarkItem bookmark = (BookmarkItem)getItem(position);

            if (convertView == null)
            {
                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.bookmark_list_item, null);
                holder.bookmark_item_title = (TextView)convertView.findViewById(R.id.bookmark_item_title);
                holder.bookmark_item_favicon = (ImageView)convertView.findViewById(R.id.bookmark_item_favicon);

                convertView.setTag(holder);
            }
            else
                holder = (ViewHolder)convertView.getTag();

            holder.bookmark_item_title.setText(bookmark.getTitle());

            if (bookmark.getFavicon() != null)
                holder.bookmark_item_favicon.setImageBitmap(getImage(bookmark.getFavicon()));
            else
                holder.bookmark_item_favicon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_bookmark));

            return convertView;
        }
    }

    static class ViewHolder {
        TextView bookmark_item_title;
        ImageView bookmark_item_favicon;
    }
   /* ADAPTER */

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
