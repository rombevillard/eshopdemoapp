package com.videodesk.eshopdemoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Created by romain@videodesk on 23/02/15.
 */

public class Category extends VdActivity {
    LinearLayout header_container = null;
    LinearLayout header;
    TextView header_title;
    TextView header_num;
    ImageView header_menu_icon;

    LinearLayout slidermenu = null;
    ImageView slidermenu_hats = null;
    ImageView slidermenu_shoes = null;
    ImageView slidermenu_glasses = null;
    ImageView slidermenu_bags = null;
    ImageView slidermenu_cart = null;

    boolean isMenuOpen;

    ScrollView scr = null;
    InputStream stream = null;

    public String category = "";
    public String[][] productInfo = new String[10][5];

    int rlId;


    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        setCategory(b.getString("cat"));

        setContentView(R.layout.category);

        LinearLayout category_container = (LinearLayout)findViewById(R.id.category_container);

        LayoutInflater inflater;
        inflater = (LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        try{
            header = (LinearLayout)inflater.inflate(R.layout.header, category_container, true);
            slidermenu = (LinearLayout)inflater.inflate(R.layout.slider_menu, null);
            category_container.addView(slidermenu, 0);
        } catch(InflateException e){
            Log.e("Inflater error********", ""+e);
        }

        /*
        SET VIEWS
         */

        slidermenu = (LinearLayout)findViewById(R.id.slidermenu);
        slidermenu_hats = (ImageView)findViewById(R.id.slidemenu_hats);
        slidermenu_shoes = (ImageView)findViewById(R.id.slidemenu_shoes);
        slidermenu_glasses = (ImageView)findViewById(R.id.slidemenu_glasses);
        slidermenu_bags = (ImageView)findViewById(R.id.slidemenu_bags);
        slidermenu_cart = (ImageView)findViewById(R.id.slidemenu_cart);


        header = (LinearLayout)findViewById(R.id.header);
        header_num = (TextView)findViewById(R.id.header_num);
        header_menu_icon = (ImageView)findViewById(R.id.header_menu_icon);
        header_title = (TextView)findViewById(R.id.header_title);

        /*
        END SET VIEWS
         */

        Typeface roboto_bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Bold.ttf");
        Typeface georgia = Typeface.createFromAsset(getAssets(), "fonts/Georgia.ttf");

        int colorId;
        CharSequence numId;
        switch (getCategory()){
            case "hats":
                colorId = getResources().getColor(R.color.color_hats);
                numId = getResources().getText(R.string.one);
                break;
            case "shoes":
                colorId = getResources().getColor(R.color.color_shoes);
                numId = getResources().getText(R.string.two);
                break;
            case "glasses":
                colorId = getResources().getColor(R.color.color_glasses);
                numId = getResources().getText(R.string.three);
                break;
            case "bags":
                colorId = getResources().getColor(R.color.color_bags);
                numId = getResources().getText(R.string.four);
                break;
            default:
                colorId = getResources().getColor(R.color.white);
                numId = "";
                break;
        }
        header.setBackgroundColor(colorId);
        header_num.setText(numId);
        slidermenu.setBackgroundColor(colorId);
        header_title.setTypeface(roboto_bold);
        header_num.setTypeface(georgia);

         /*
        SET HANDLER
         */
        slidermenu_hats.setOnClickListener(handler);
        slidermenu_shoes.setOnClickListener(handler);
        slidermenu_glasses.setOnClickListener(handler);
        slidermenu_bags.setOnClickListener(handler);
        slidermenu_cart.setOnClickListener(handler);
        header.setOnClickListener(handler);
        header_menu_icon.setOnClickListener(handler);
        /*
        END SET HANDLER
         */

        /*
        OPEN XML STREAM AND PARSE CONTENT (cat from Bundle)
         */
        try{
            String lang = getResources().getConfiguration().locale.getLanguage();
            switch(getCategory()){
                case "hats":
                    stream = getApplicationContext().getAssets().open("xml/hats-"+lang+".xml");
                    break;
                case "shoes":
                    stream = getApplicationContext().getAssets().open("xml/shoes-"+lang+".xml");
                    break;
                case "glasses":
                    stream = getApplicationContext().getAssets().open("xml/glasses-"+lang+".xml");
                    break;
                case "bags":
                    stream = getApplicationContext().getAssets().open("xml/bags-"+lang+".xml");
                    break;
            }
            XmlPullParserFactory factory;
            factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(stream, null);

           category_container.addView(parseXml(parser));
            //scr.addView(header_container);
        } catch ( IOException|XmlPullParserException e ) {
            e.printStackTrace();
        }
    }

    private ScrollView parseXml(XmlPullParser xpp) throws XmlPullParserException, IOException{
        int e = xpp.getEventType();
        String name = null;
        int previousEvent = 0;
        String previousText = null;

        ScrollView scrollView = new ScrollView(this);
        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setWeightSum(10);
        ll.setBackgroundColor(Color.parseColor("#E7E7E7"));
        ll.setGravity(Gravity.CENTER);

        RelativeLayout rl = null;
        int rlId = 1;
        TextView title = null;
        TextView description = null;
        TextView price = null;
        ImageView img = null;


        while (e != XmlPullParser.END_DOCUMENT){
            name = xpp.getName();
            switch(e){
                case XmlPullParser.START_DOCUMENT:
                   //Log.d("log", "Document Started");
                    break;

                case XmlPullParser.START_TAG:
                    //Log.d("log", "<"+name+">");

                    if("product".equals(xpp.getName())){
                        rl = new RelativeLayout(this);

                        //set Relative Layout rules
                        //lp.addRule(RelativeLayout.END_OF, rlId);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(pxFromDp(this, 20), pxFromDp(this, 10), pxFromDp(this, 20), pxFromDp(this, 10));

                        rl.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        rl.setLayoutParams(lp);
                        rl.setPadding(10,10,10,10);

                        rl.setId(rlId);
                        //Log.d("SETID*************", "Just created Relative Layout with id"+rlId);
                        rlId++;
                    }
                    previousText = xpp.getName();
                    break;

                case XmlPullParser.TEXT:
                    if(previousEvent != XmlPullParser.END_TAG && !"".equals(xpp.getText())){
                        switch(previousText){
                            case "name":
                                //Log.d("log", "Product name: "+xpp.getText());
                                setStringBundle(rl.getId(), 0, xpp.getText());
                                //Log.d("Bundle", "Set productInfo["+rl.getId()+"][0] to: "+getString(rl.getId(), 0));

                                title = new TextView(this);
                                title.setText(xpp.getText());
                                title.setTextColor(Color.BLACK);
                                title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Georgia.ttf"));
                                title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

                                RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                titleParams.setMargins(pxFromDp(this, 150), pxFromDp(this, 30), 0, 0);

                                title.setLayoutParams(titleParams);

                                if(rl != null){
                                    rl.addView(title);
                                }
                                break;
                            case "description":
                                //Log.d("log", "Product description: "+xpp.getText());
                                setStringBundle(rl.getId(), 1, xpp.getText());
                                //Log.d("Bundle", "Set productInfo["+rl.getId()+"][1] to: "+getString(rl.getId(), 1));

                                description = new TextView(this);

                                description.setText(xpp.getText());
                                description.setTextColor(Color.BLACK);
                                description.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto_Regular.ttf"));
                                description.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                                description.setId(View.generateViewId());

                                RelativeLayout.LayoutParams descParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                descParams.setMargins(pxFromDp(this, 150), pxFromDp(this, 80), 0, 0);

                                description.setLayoutParams(descParams);

                                if(rl != null){
                                    rl.addView(description);
                                }
                                break;
                            case "long":
                                setStringBundle(rl.getId(), 2, xpp.getText());
                                //Log.d("Bundle", "Set productInfo["+rl.getId()+"][2] to: "+getString(rl.getId(), 2));
                                break;
                            case "price":
                                //Log.d("log", "Product price: "+xpp.getText());
                                setStringBundle(rl.getId(), 3, xpp.getText());
                                //Log.d("Bundle", "Set productInfo["+rl.getId()+"][3] to: "+getString(rl.getId(), 3));

                                price = new TextView(this);
                                price.setText("$ "+xpp.getText());
                                price.setTextColor(Color.BLACK);
                                price.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Georgia.ttf"));
                                price.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

                                RelativeLayout.LayoutParams priceParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                if(description != null){
                                    priceParams.setMargins(pxFromDp(this, 150), pxFromDp(this, 20), 0, pxFromDp(this, 10));
                                    priceParams.addRule(RelativeLayout.BELOW, description.getId());
                                }


                                price.setLayoutParams(priceParams);

                                if(rl != null){
                                    rl.addView(price);
                                }
                                break;
                            case "image":
                                //Log.d("log", "Product image URL: "+xpp.getText());
                                setStringBundle(rl.getId(), 4, xpp.getText());
                                //Log.d("Bundle", "Set productInfo["+rl.getId()+"][4] to: "+getString(rl.getId(), 4));

                                String imgUrl = xpp.getText();

                                img = new ImageView(this);
                                int drawableId = this.getResources().getIdentifier(imgUrl, "drawable", this.getPackageName());
                                img.setImageResource(drawableId);


                                RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                imgParams.width = pxFromDp(this, 100);
                                imgParams.height = pxFromDp(this, 100);
                                imgParams.setMargins(pxFromDp(this, 30),0,0,0);
                                imgParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                                imgParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

                                img.setLayoutParams(imgParams);

                                //Log.d("Info", "DrawableId = " + drawableId);
                                if(rl != null){
                                    rl.addView(img);
                                }
                                break;
                            default:
                                //Log.d("log", "Text not found");
                                break;
                        }
                        if(rl != null) {
                            rl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int id = 0;
                                    for (int i = 0; i < productInfo[0].length; i++){
                                        if (i == v.getId()){
                                            id = i;
                                        }
                                    }
                                    //Log.d("RL ID******************", "id =" + id);
                                    Intent i = new Intent(Category.this, Product.class);
                                    Bundle b = new Bundle();
                                    b.putStringArray("productInfo", getStringBundle(id));//must not use 0 as an id
                                    b.putString("cat", getCategory());
                                    i.putExtras(b);
                                    startActivity(new Intent(getBaseContext(), Product.class).putExtras(b));
                                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    //finish();
                                }
                            });
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    //Log.d("log", "</"+xpp.getName()+">");

                    if("product".equals(xpp.getName())){
                        ll.addView(rl);
                }
                    break;
            }
            previousEvent = xpp.getEventType();

            e = xpp.next();
        }
        scrollView.addView(ll);
        return scrollView;
    }

    /*
    TOOLS
     */

    /*
        slider menu animation
     */

    /*public static int dpFromPx(final Context context, final float px) {
        return Math.round(px / context.getResources().getDisplayMetrics().density);
    }

    public static int pxFromDp(final Context context, final float dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    public void expand(final View v){
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = pxFromDp(this.getApplicationContext(), 75);

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);

        Animation a = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1 ? targetHeight : (int)(targetHeight*interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(500);
        v.startAnimation(a);
    }

    public void collapse(final View v){
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }
                else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(500);
        v.startAnimation(a);
    }*/
    /*
        end slider menu animation
     */

    /*
    END TOOLS
     */

    /***************************
     * GETTERS / SETTERS
     */

    public void setCategory(String str){
        category = str;
    }

    public String getCategory(){
        return category;
    }

    public void setStringBundle(int id, int index, String str){
        productInfo[id][index] = str;
    }

    public String getString(int id, int index){
        return productInfo[id][index];
    }

    public String[] getStringBundle(int id){
        return productInfo[id];
    }

    /*****************
     * END GETTERS / SETTERS
     */

    /*
    HANDLER
     */
    View.OnClickListener handler = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if (v == slidermenu_hats){
                Intent i = new Intent(getApplicationContext(), Category.class);
                Bundle b = new Bundle();
                b.putString("cat", "hats");
                i.putExtras(b);
                startActivity(i);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
            else if (v == slidermenu_shoes){
                Intent i = new Intent(getApplicationContext() , Category.class);
                Bundle b = new Bundle();
                b.putString("cat", "shoes");
                i.putExtras(b);
                startActivity(i);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
            else if (v == slidermenu_glasses){
                Intent i = new Intent(getApplicationContext(), Category.class);
                Bundle b = new Bundle();
                b.putString("cat", "glasses");
                i.putExtras(b);
                startActivity(i);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
            else if(v == slidermenu_bags){
                Intent i = new Intent(getApplicationContext(), Category.class);
                Bundle b = new Bundle();
                b.putString("cat", "bags");
                i.putExtras(b);
                startActivity(i);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
            else if(v == slidermenu_cart){
                Intent i = new Intent(getApplicationContext(), Cart.class);
                startActivity(i);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
            else if(v == header_menu_icon){
                launchMenuAnimation(slidermenu);
            }

            //must finish activity on header click
            if (v.getParent() == slidermenu){
                finish();
            }
        }
    };
    /*
    END HANDLER
     */
}
