package com.drawinggame;

import android.content.Context;

public class ImageLibrary {
	private ImageLoader imageLoader;
    public ImageLibrary(Context contextSet)
    {
    	imageLoader = new ImageLoader(contextSet);
    }
}
