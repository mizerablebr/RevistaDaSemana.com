package com.rds.revistadasemanacom;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brunomorais on 12/01/16.
 */
public class RevistaDaSemanaXmlParser {

    private static final String ns = null;

    /*
    * The next step is to instantiate a parser and kick off the parsing process.
    * In this snippet, a parser is initialized to not process namespaces,
    * and to use the provided InputStream as its input. It starts the parsing process with a call to nextTag()
    * and invokes the readFeed() method, which extracts and processes the data the app is interested in
    * */

    public List<PostData> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<PostData> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<PostData> entries  = new ArrayList<PostData>();

        parser.require(XmlPullParser.START_TAG, ns, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d("RevistaParser","parser.getName: " + name);
            //Start by looking for the item tag
            if (name.equals("item")) {
                Log.d("RevistaParser", "found tag ITEM");
                entries.add(readPostData(parser));
            } else if (name.equals("channel")) {
                parser.next();
            } else {
                Log.d("RevistaParser","NOT found tag ITEM");
                skip(parser);
            }
        }
        return entries;
    }

    /*
    Parses the contents of a Post. If it encounter a title and link tag, hands them off to their
    respective "read" methods for processing. Otherwise, skips the tag:
     */
    private PostData readPostData (XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String title = null;
        String link = null;
        String category = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("link")) {
                link = readLink(parser);
            } else if (name.equals("category")) {
                category = readCategory(parser);
                Log.d("readPostData","category: " + category);
            } else {
                skip(parser);
            }
        }
        return new PostData(title, link, category);
    }

    //Processes title tags in the feed
    private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    //Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }
    private String readCategory(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "category");
        String category = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "category");
        return category;
    }

    //For the tags title and link, extracts their text values.
    private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /* Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
     if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
     finds the matching END_TAG (as indicated by the value of "depth" being 0)
    */
    private void skip (XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


}
