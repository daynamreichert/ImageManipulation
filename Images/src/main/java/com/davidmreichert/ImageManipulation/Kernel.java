package com.davidmreichert.ImageManipulation;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * A grid of values for performing different filters,
 * blurs, and other image manipulations.
 * 
 * @author david.reichert
 *
 */
public class Kernel {
	/**
	 * enum for constructing different types of Kernel
	 */
	public enum Type {
		MEAN,
		GAUSSIAN
	}
	
	// Grid stored in a 2D byte array
	private short[][] grid;
	
	// Default radius is 1 (3x3 grid)
	private int radius = 1;
	int diameter = (radius * 2) + 1;
	
	// default type is mean
	private Type kernelType = Type.MEAN;
	
	/**
	 * Uses default radius (1) to initialize the grid and type
	 * (Mean) to construct the grid
	 */
	public Kernel() {
		init_grid();
	}
	
	public Kernel(int radius, Type type) {
		this.radius = radius;
		this.kernelType = type;
		this.diameter = radius * 2 + 1;
		init_grid();
	}

	/**
	 * Initializes the grid structure using the radius
	 */
	private void init_grid() {
		int diameter = (radius << 1) + 1;
		grid = new short[diameter][diameter];
		
		switch(kernelType) {
			case MEAN:
				// Fills with one - get average around a pixel
				IntStream.range(0, diameter).forEach(i -> {
					Arrays.fill(grid[i], (short)1);
				});
				break;
			case GAUSSIAN:
				int center = diameter/2;
				int lastIndex = diameter - 1;
				
				// Sets values around grid based on distance from the center
				IntStream.range(0, center + 1).forEach(i -> {
					IntStream.range(0, center + 1).forEach(j -> {
						short value = (short)(1 << (i + j));
						grid[i][j] = value;
						grid[lastIndex - i][lastIndex - j] = value;
						grid[i][lastIndex - j] = value;
						grid[lastIndex - i][j] = value;
					});
				});
				
				break;
			default:
				System.err.println("Kernel type not valid. defaulting to MEAN");
				
				// Fills with one - get average around a pixel
				IntStream.range(0, diameter).forEach(i -> {
					Arrays.fill(grid[i], (short)1);
				});
		} 
	}
	
	/**
	 * Gets coordinates relative to the center of the grid.
	 * Throws exception if x and y are out of bounds
	 * @param relX: distance from center in the x direction
	 * @param relY: distance from center in the y direction
	 * @return value of the grid at that location
	 * @throws IndexOutOfBoundsException: if coordinate not in grid.
	 * 
	 */
	public int get_relative_value(int relX, int relY) {
		// If not in grid, return 0 (don't count pixel)
		if (Math.abs(relX) > radius || Math.abs(relY) > radius) {
			throw new IndexOutOfBoundsException("Coordinate not in Kernel grid");
		}
		
		// return pixel at given position from center
		int center = diameter / 2;
		return grid[center + relY][center + relX];
	}
	
	/**
	 * Gets the coordinates relative to the upper left corner
	 * of the grid. If x and y are out of bounds, throws OutOfBounds
	 * exception.
	 * @param x: horizontal position on the grid
	 * @param y: vertical position on the grid
	 * @return value of the grid at that location	 
	 * @throws IndexOutOfBoundsException: if coordinate not in grid.
	 */
	public int get_coordinate_value(int x, int y) {
		// If not in grid, throw exception
		if (x > radius || x < 0 || y > radius || y < 0) {
			throw new IndexOutOfBoundsException("Coordinate not in Kernel grid");
		}
		
		// Otherwise, just return the value at that point
		return grid[y][x];
	}
	
	/**
	 * Converts kernel to string
	 */
	@Override
	public String toString() {
		// Gets max number of digits in the grid numbers
		int maxDigits = (kernelType == Type.GAUSSIAN) 
				? ((1 << (2 * radius)) + "").length()
				: 1;
				
		// Builds the grid string
		StringBuilder gridString = new StringBuilder();
		gridString.append("\n");
		IntStream.range(0, diameter).forEach(i -> {
			IntStream.range(0, diameter).forEach(j -> {
				gridString.append(String.format("%1$"+ (maxDigits + 1) + "s", grid[i][j]));
			});
			gridString.append("\n");
		});
		
		// Puts labels and returns
		return String.format("Type: %s\n"
				+ "grid: %s", 
				kernelType,
				gridString);
	}
	
}
