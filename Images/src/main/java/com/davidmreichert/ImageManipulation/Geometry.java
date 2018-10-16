package com.davidmreichert.ImageManipulation;

import java.awt.Color;

/**
 * Contains a variety of classes for representing one to 
 * two dimensional object in an image
 * @author davidmreichert
 *
 */
public class Geometry {
	/**
	 * Represents a pixel in an image. Contains x and y coordinates
	 * as well as the color
	 */
	public static class Pixel {
		/**
		 * x (horizontal) and y (vertical) coordinates in an image
		 */
		private int x, y;
		
		/**
		 * Color of the pixel
		 */
		private Color color;
		
		/**
		 * Constructor for a {@link Pixel} object. Sets variables to
		 * default. If variables accessed in this state,
		 * bad things happen
		 */
		public Pixel() {
			x = -1;
			y = -1;
			color = new Color(0, false); 
		}
		
		/**
		 * Constructor for a {@link Pixel} object.
		 * @param x: x coordinate of the pixel
		 * @param y: y coordinate of the pixel
		 * @param rgb: color of the pixel
		 */
		public Pixel(int x, int y, int rgb) {
			this.x = x;
			this.y = y;
			color = new Color(rgb);
		}
		
		/* Getters and setters */
		
		/**
		 * Gets the x coordinate of the pixel
		 */
		public int get_x() {
			return x;
		}
		
		/**
		 * Sets the x coordinate of the pixel
		 * @param xCoord: new x coordinate
		 */
		public void set_x(int xCoord) {
			this.x = xCoord;
		}
		
		/**
		 * Gets the x coordinate of the pixel
		 */
		public int get_y() {
			return y;
		}
		
		/**
		 * Sets the y coordinate of the pixel
		 * @param yCoord: new y coordinate
		 */
		public void set_y(int yCoord) {
			this.x = yCoord;
		}
		
		/**
		 * Gets the @{link Color} of the pixel
		 */
		public Color get_color() {
			return color;
		}
		
		/**
		 * Sets the {@link Color} of the pixel
		 * @param rgb: binary representation of the color
		 * at a specific point. 
		 */
		public void set_color(int rgb) {
			color = new Color(rgb);
		}
	}

}
