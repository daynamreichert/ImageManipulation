package com.davidmreichert.ImageManipulation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import javax.imageio.ImageIO;

import com.davidmreichert.ImageManipulation.Geometry.Pixel;

/**
 * Image manipulator for brightening/darkening, contrasting, 
 * and pointalizing an image
 *
 *@author davidmreichert
 */
public class ImageManipulator 
{
	// Black rgb is 0
	public static final int BLACK = Color.BLACK.getRGB();
	public static final int WHITE = Color.WHITE.getRGB();
	public static final int RED = Color.RED.getRGB();
	public static final int GREEN = Color.GREEN.getRGB();
	public static final int BLUE = Color.BLUE.getRGB();
	
	// Max distance between two colors
	public static final double MAX_DISTANCE = Math.sqrt(3 * (255*255));
	
	/**
	 * Empty constructor
	 */
	public ImageManipulator() {
		
	}
	
	/* Helper Functions */
	
	/**
	 * Iterates over an image in parallel and performs the predicate
	 * @param image: {@link BufferedImage} to be iterated over
	 * @param function: {@link Consumer} to be done on the image
	 */
	private void iterate_over_image(BufferedImage image, BiConsumer<Integer, Integer> consumer) {
		// Streams over the y values
		IntStream.iterate(0, i -> i + 1)
			.limit(image.getHeight())
			.parallel()
			.forEach(y -> 
				IntStream
					.iterate(0, i -> i + 1)
					.limit(image.getWidth())
					.parallel()
					.forEach(x -> consumer.accept(x, y))
					);
	}
	
	/**
	 * Returns the Euclidean distance between two colors using the
	 * red, green, and blue values.
	 * @param pixel: Original color
	 * @param offset: new color
	 * @return Euclidean distance between the two
	 */
	private double distance(Color pixel, Color offset) {
		double rDist = pixel.getRed() - offset.getRed();
		double gDist = pixel.getGreen() - offset.getGreen();
		double bDist = pixel.getBlue() - offset.getBlue();
		return Math.sqrt((rDist*rDist) + (gDist*gDist) + (bDist*bDist));
	}

	/**
	 * Checks if an x,y coordinate is within the bounds of an image
	 * @param image: {@link BufferedImage} to be checked
	 * @param currX: x coordinate
	 * @param currY: y coordinate
	 * @return boolean if x and y are both in the image
	 */
	private boolean in_bounds(BufferedImage image, int currX, int currY) {
		return (currX >= 0 && currY >= 0 && currX < image.getWidth() && currY < image.getHeight());
	}
	
	
	/* Image manipulation functions */
	
	/**
	 * Emphasizes edges and points of interest
	 * @param image: a {@link BufferedImage} created from an image file
	 * @param distance: distance between two colors for it to be 
	 * 					considered a point of interest
	 * @return a {@link BufferedImage} of just edges
	 */
	public BufferedImage pointalize(BufferedImage image, int distance, int radius) {
		// Gets height and width of the image
		int height = image.getHeight(), width = image.getWidth();
		
		// Total pixels around a pixel for a given radius
		double totalSurrounding = (radius * radius) - 1;
		
		// Creates new buffered image to store files
		BufferedImage imageEdges = new BufferedImage(width, height, image.getType());
		
		// Runs through image to detect edges
		List<Pixel> pixels = Collections.synchronizedList(new ArrayList<>());
		iterate_over_image(image, (x,y) -> {
			Pixel pixel = new Pixel(
					x,
					y,
					image.getRGB(x, y)
			);
			
			double totalColDistance = 0;
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dx = -radius; dx <= radius; dx++) {
					if (dx == 0 && dy == 0) continue;
					int currX = x + dx, currY = y + dy;
					if (in_bounds(image, currX, currY)) {
						Color currColor = new Color(image.getRGB(currX, currY));
						
						int picDistance = dy*dy + dx*dx;
						totalColDistance += distance(pixel.get_color(), currColor) / picDistance;
					}
				}
			}
			
			if (totalColDistance / totalSurrounding > distance) {
				pixel.set_color(BLACK);
			} else {
				pixel.set_color(WHITE);
			}

			pixels.add(pixel);
		});
		
		pixels.parallelStream().forEach(pixel -> {
			imageEdges.setRGB(
					pixel.get_x(),
					pixel.get_y(),
					pixel.get_color().getRGB());
		});

		return imageEdges;

	}
	
	/**
	 * increase contrast an image by increasing the colors
	 * @param saturation: what to multiply the colors by
	 */
	public BufferedImage brighten(BufferedImage image, double brightenRatio) {
		// Creates new buffered image to store files
		BufferedImage imageEdges = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		
		// Runs through image and multiplies the colors
		List<Pixel> pixels = Collections.synchronizedList(new ArrayList<>());
		iterate_over_image(image, (x,y) -> {
			// Gets the current color and multiplies it
			Color color = new Color(image.getRGB(x, y));
			int newBlue = (int) (color.getBlue() * brightenRatio);
			int newRed = (int) (color.getRed() * brightenRatio);
			int newGreen = (int) (color.getGreen() * brightenRatio);
			color = new Color(
					(newRed > 255) ? 255 : newRed,
					(newGreen > 255) ? 255 : newGreen,
					(newBlue > 255) ? 255 : newBlue
				);
			
			Pixel pixel = new Pixel(
					x,
					y,
					color.getRGB()
			);
			
			

			pixels.add(pixel);
		});
		
		pixels.parallelStream().forEach(pixel -> {
			imageEdges.setRGB(
					pixel.get_x(),
					pixel.get_y(),
					pixel.get_color().getRGB());
		});

		return imageEdges;
	}
	
	
	/**
	 * increase contrast an image by increasing the colors
	 * @param image: {@link BufferedImage} to increase the contrast of
	 * @param contrastRatio: what to multiply the colors by
	 * @return {@link BufferedImage} with the colors contrasted
	 */
	public BufferedImage increase_contrast(BufferedImage image, double contrastRatio) {
		double actualContrastRatio = contrastRatio / 10;
		
		// Creates new buffered image to store files
		BufferedImage imageEdges = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		
		// Runs through image and multiplies the colors
		List<Pixel> pixels = Collections.synchronizedList(new ArrayList<>());
		iterate_over_image(image, (x,y) -> {
			// Gets the current color and multiplies it
			Color color = new Color(image.getRGB(x, y));
			int newBlue = (int) (color.getBlue() * (color.getBlue() * actualContrastRatio));
			int newRed = (int) (color.getRed() * (color.getRed() * actualContrastRatio));
			int newGreen = (int) (color.getGreen() * (color.getGreen() * actualContrastRatio));
			color = new Color(
					(newRed > 255) ? 255 : newRed,
					(newGreen > 255) ? 255 : newGreen,
					(newBlue > 255) ? 255 : newBlue
				);
			
			Pixel pixel = new Pixel(
					x,
					y,
					color.getRGB()
			);
			
			

			pixels.add(pixel);
		});
		
		pixels.parallelStream().forEach(pixel -> {
			imageEdges.setRGB(
					pixel.get_x(),
					pixel.get_y(),
					pixel.get_color().getRGB());
		});

		return imageEdges;
	}



	public static void main(String[] args) {
		ImageManipulator im = new ImageManipulator();
		
		File file = new File("src/main/resources/SeanTest.jpg");
		
		try {
			BufferedImage image = ImageIO.read(file);
			BufferedImage imagePoints = im.pointalize(image, 10, 4);
			
			File outputFile = new File("src/main/resources/SeanPoints.jpg");
			ImageIO.write(imagePoints, "jpeg", outputFile);
			
			BufferedImage imageBright = im.brighten(image, 2);
			
			outputFile = new File("src/main/resources/SeanBrighten.jpg");
			ImageIO.write(imageBright, "jpeg", outputFile);
			
			BufferedImage imageContrast = im.increase_contrast(image, 0.15);
			
			outputFile = new File("src/main/resources/SeanContrast.jpg");
			ImageIO.write(imageContrast, "jpeg", outputFile);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
