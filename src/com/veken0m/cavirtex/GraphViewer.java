package com.veken0m.cavirtex;

import java.text.NumberFormat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.View;

/**
 * GraphView creates a scaled line or bar graph with x and y axis labels.
 * 
 * @author Arno den Hond
 * 
 *         Note from Dest: this class is undocumented and not easily scalable
 *         (boolean value for graph type...really?) May completely write this
 *         class or use a different library.
 * 
 *         Todo: Make the title and axis text bigger Add more values to y axis
 *         Colors Dots where lines end
 * 
 */
public class GraphViewer extends View {

	public static boolean BAR = true;
	public static boolean LINE = false;

	private Paint paint;
	private float[] values;
	private String[] horlabels;
	private String[] verlabels;
	private String title;
	private boolean type;

	/**
	 * If we pass in a min and max to the constructor, store it here and tell
	 * the program that the bounds are already set.
	 */
	private float gMin;
	private float gMax;
	private boolean bBoundsSet = false;

	public GraphViewer(Context context, float[] values, String title,
			String[] horlabels, String[] verlabels, boolean type) {
		super(context);
		if (values == null)
			values = new float[0];
		else
			this.values = values;
		if (title == null)
			title = "";
		else
			this.title = title;
		if (horlabels == null)
			this.horlabels = new String[0];
		else
			this.horlabels = horlabels;
		if (verlabels == null)
			this.verlabels = new String[0];
		else
			this.verlabels = verlabels;
		this.type = type;
		paint = new Paint();
	}

	/**
	 * Added a min and max input, because we calculate that beforehand sometimes
	 * 
	 * @modified Dest
	 */
	public GraphViewer(Context context, float[] values, String title,
			String[] horlabels, String[] verlabels, boolean type, float pMin,
			float pMax) {
		super(context);
		if (values == null)
			values = new float[0];
		else
			this.values = values;
		if (title == null)
			title = "";
		else
			this.title = title;
		if (horlabels == null)
			this.horlabels = new String[0];
		else
			this.horlabels = horlabels;
		if (verlabels == null)
			this.verlabels = new String[0];
		else
			this.verlabels = verlabels;
		this.type = type;

		gMin = pMin;
		gMax = pMax;
		bBoundsSet = true;

		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float border = 20;
		float horstart = border * 2;
		float height = getHeight();
		float width = getWidth() - 1;

		paint.setTextSize(17.0f);

		float max;
		float min;
		if (bBoundsSet) {
			max = gMax;
			min = gMin;

		} else {
			max = getMax();
			min = getMin();
		}

		float diff = max - min;
		float graphheight = height - (2 * border);
		float graphwidth = width - (2 * border);

		paint.setTextAlign(Align.LEFT);
		int vers = verlabels.length - 1;
		for (int i = 0; i < verlabels.length; i++) {
			paint.setColor(Color.DKGRAY);
			float y = ((graphheight / vers) * i) + border;
			canvas.drawLine(horstart, y, width, y, paint);
			paint.setColor(Color.WHITE);
			canvas.drawText(verlabels[i], 0, y, paint);
		}
		int hors = horlabels.length - 1;
		for (int i = 0; i < horlabels.length; i++) {
			paint.setColor(Color.DKGRAY);
			float x = ((graphwidth / hors) * i) + horstart;
			canvas.drawLine(x, height - border, x, border, paint);
			paint.setTextAlign(Align.CENTER);
			if (i == horlabels.length - 1)
				paint.setTextAlign(Align.RIGHT);
			if (i == 0)
				paint.setTextAlign(Align.LEFT);
			paint.setColor(Color.rgb(51, 181, 229));// 51,181,229
			canvas.drawText(horlabels[i], x, height - 4, paint);
		}

		paint.setTextAlign(Align.CENTER);
		canvas.drawText(title, (graphwidth / 2) + horstart, border - 4, paint);

		if (max != min) {
			paint.setColor(Color.GREEN);
			if (type == BAR) {
				float datalength = values.length;
				float colwidth = (width - (2 * border)) / datalength;
				for (int i = 0; i < values.length; i++) {
					float val = values[i] - min;
					float rat = val / diff;
					float h = graphheight * rat;
					canvas.drawRect((i * colwidth) + horstart, (border - h)
							+ graphheight, ((i * colwidth) + horstart)
							+ (colwidth - 1), height - (border - 1), paint);
				}
			} else {
				float datalength = values.length;
				float colwidth = (width - (2 * border)) / datalength;
				float halfcol = colwidth / 2;
				float lasth = 0;
				for (int i = 0; i < values.length; i++) {
					float val = values[i] - min;
					float rat = val / diff;
					float h = graphheight * rat;
					if (i > 0)
						canvas.drawLine(((i - 1) * colwidth) + (horstart + 1)
								+ halfcol, (border - lasth) + graphheight,
								(i * colwidth) + (horstart + 1) + halfcol,
								(border - h) + graphheight, paint);
					lasth = h;
				}
			}
		}
	}

	private float getMax() {
		float largest = Integer.MIN_VALUE;
		for (int i = 0; i < values.length; i++)
			if (values[i] > largest)
				largest = values[i];
		return largest;
	}

	private float getMin() {
		float smallest = Integer.MAX_VALUE;
		for (int i = 0; i < values.length; i++)
			if (values[i] < smallest)
				smallest = values[i];
		return smallest;
	}

	/**
	 * This function creates an array of labels based on the input values
	 * 
	 * @author Dest
	 * 
	 * @param pMin
	 *            min values
	 * @param pMax
	 *            max value
	 * @param pNum
	 *            number of labels we want to return
	 * @param pPre
	 *            what goes before the value
	 * @param pPost
	 *            what goes after the value
	 * @return String[]
	 */
	public static String[] createLabels(float pMin, float pMax, int pNum,
			String pPre, String pPost, int pDecimals) {
		int places = pDecimals;
		if (places < 0)
			places = 0;

		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(places);

		int num = pNum;
		float max = pMax;
		float min = pMin;
		if (num <= 2)
			num = 3;

		/**
		 * Someone didn't pay attention to params, lets switch them around
		 */
		if (max < min) {
			float a = max;
			max = min;
			min = a;
		}

		float range = max - min;
		float stepSize = (range / (num - 1));

		float track = min;

		String[] ret = new String[num];

		ret[0] = pPre + numberFormat.format(max) + pPost;

		for (int i = num - 2; i > 0; i--) {
			track += stepSize;
			ret[i] = pPre + numberFormat.format(track) + pPost;
		}

		ret[num - 1] = pPre + numberFormat.format(min) + pPost;

		return ret;
	}

}
