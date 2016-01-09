package com.sqiwy.menu.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by abrysov
 */

public class WeightBlockCH extends WeightBlock {

	public static final int TYPE_SMALL_MEDIUM = 0x05;
	public static final int TYPE_BIG_TWO_SMALL = 0x06;
	public static final int TYPE_TWO_TEXT = 0x07;

	public enum WeightBlockType {
		TYPE_SMALL_MEDIUM, TYPE_BIG_TWO_SMALL,TYPE_TWO_TEXT
	}

	public  WeightBlockCH(int i, ArrayList<WeightItem> blockitems) {
		super(i, blockitems);
		// TODO Auto-generated constructor stub
	}

	public static ArrayList< WeightBlockCH> buildBlocks(
			ArrayList<WeightItem> items) {
		ArrayList< WeightBlockCH> a = new ArrayList< WeightBlockCH>();
		Collections.sort(items);
		// number of small, medium and big blocks
		int[] numBlocks = new int[4];
		int state = 0;
		int posSmall = 0;
		int posBig = 0, posMedium = 0;
		int k = 0;
		for (WeightItem item : items) {

			if (item.getItemWeight() == 1 && state == 0) {
				state = 1;
				posSmall = k;
			} else if (item.getItemWeight() == 2 && state == 1) {
				state = 2;
				posMedium = k;
			} else if (item.getItemWeight() == 3 && state == 2) {
				state = 3;
				posBig = k;
			}
			numBlocks[item.getItemWeight()] += 1;
			k++;
		}
		int numOfNotTextItems = items.size() - numBlocks[0];
		System.out.println(items);
		System.out.println(Arrays.toString(numBlocks));
		if ((numOfNotTextItems - 3 * numBlocks[3]) % 2 == 1) {
			items.get(posBig - 1).setItemWeight(3);
			posBig--;
			numBlocks[3]++;
			numBlocks[2]--;
		}
		System.out.println(items);
		System.out.println(Arrays.toString(numBlocks));
		int numMed = (numOfNotTextItems - 3 * numBlocks[3]) / 2;
		System.out.println(numMed);
		if (numBlocks[2] < numMed) {
			while (numBlocks[2] != numMed) {
				numBlocks[1]--;
				numBlocks[2]++;
				items.get(posMedium - 1).setItemWeight(2);
				posMedium--;
			}
		} else if (numBlocks[2] > numMed) {
			while (numBlocks[2] != numMed) {
				numBlocks[1]++;
				numBlocks[2]--;
				items.get(posMedium + 1).setItemWeight(1);
				posMedium++;
			}
		}
		System.out.println(items);
		System.out.println(Arrays.toString(numBlocks));

		ArrayList<WeightItem> blockitems;
		 WeightBlockCH wb;
		int indText = 0;
		int indSmall = posSmall;
		int indBig = posBig;
		int indMedium = posMedium;
		int i;
		int minitems = numBlocks[3] < numBlocks[2] ? numBlocks[3]
				: numBlocks[2];
		int maxitems = numBlocks[2] < numBlocks[3] ? numBlocks[3]
				: numBlocks[2];
		int zerkalo1 = 1;
		int zerkalo2 = 0;
		for (i = 0; i < minitems; i++) {
			blockitems = new ArrayList<WeightItem>();
			
			blockitems.add(items.get(indSmall));
			blockitems.add(items.get(indSmall + 1));
			if (zerkalo1 == 1) {
				blockitems.add(items.get(indBig));
				zerkalo1 = 0;
			}
			else {
				blockitems.add(0, items.get(indBig));
				zerkalo1 = 1;
			}
			indSmall += 2;
			indBig += 1;
			wb = new  WeightBlockCH(TYPE_BIG_TWO_SMALL, blockitems);
			a.add(wb);
			blockitems = new ArrayList<WeightItem>();
			blockitems.add(items.get(indSmall));
			if (zerkalo2 == 1) {
				blockitems.add(items.get(indMedium));
				zerkalo2 = 0;
			}
			else {
				blockitems.add(0, items.get(indMedium));
				zerkalo2 = 1;
			}
			indSmall += 1;
			indMedium += 1;
			wb = new  WeightBlockCH(TYPE_SMALL_MEDIUM, blockitems);
			a.add(wb);
			
			if (indText != posSmall) {
				blockitems = new ArrayList<WeightItem>();
				blockitems.add(items.get(indText));
				indText += 1;
				if (indText != posSmall) {
					blockitems.add(items.get(indText));
					indText += 1;
				}
				wb = new WeightBlockCH(TYPE_TWO_TEXT, blockitems);
				a.add(wb);
			}

		}
		if (minitems == numBlocks[2]) {
			for (i = minitems; i < maxitems; i++) {
				blockitems = new ArrayList<WeightItem>();
				blockitems.add(items.get(indSmall));
				if (zerkalo2 == 1) {
					blockitems.add(items.get(indMedium));
					zerkalo2 = 0;
				}
				else {
					blockitems.add(0, items.get(indMedium));
					zerkalo2 = 1;
				}
				indSmall = indSmall + 1;
				indMedium += 1;
				wb = new  WeightBlockCH(TYPE_SMALL_MEDIUM, blockitems);
				a.add(wb);
				if (indText != posSmall) {
					blockitems = new ArrayList<WeightItem>();
					blockitems.add(items.get(indText));
					indText += 1;
					if (indText != posSmall) {
						blockitems.add(items.get(indText));
						indText += 1;
					}
					wb = new  WeightBlockCH(TYPE_TWO_TEXT, blockitems);
					a.add(wb);
				}
			}
		} else {
			for (i = minitems; i < maxitems; i++) {
				blockitems = new ArrayList<WeightItem>();
				blockitems.add(items.get(indSmall));
				blockitems.add(items.get(indSmall + 1));
				if (zerkalo1 == 1) {
					blockitems.add(items.get(indBig));
					zerkalo1 = 0;
				}
				else {
					blockitems.add(0, items.get(indBig));
					zerkalo1 = 1;
				}				
				indSmall = indSmall + 2;
				indBig += 1;
				wb = new  WeightBlockCH(TYPE_BIG_TWO_SMALL, blockitems);
				a.add(wb);
				if (indText != posSmall) {
					blockitems = new ArrayList<WeightItem>();
					blockitems.add(items.get(indText));
					indText += 1;
					if (indText != posSmall) {
						blockitems.add(items.get(indText));
						indText += 1;
					}
					wb = new  WeightBlockCH(TYPE_TWO_TEXT, blockitems);
					a.add(wb);
				}
			}
		}

		return a;

	}

	public static void main(String[] args) {
		ArrayList<WeightItem> a = new ArrayList<WeightItem>();
		a.add(new WeightItem("1", 1));
		a.add(new WeightItem("1", 2));
		a.add(new WeightItem("1", 2));
		a.add(new WeightItem("1", 3));
		a.add(new WeightItem("1", 3));
		a.add(new WeightItem("1", 2));
		a.add(new WeightItem("1", 3));
		a.add(new WeightItem("1", 1));
		a.add(new WeightItem("1", 1));
		a.add(new WeightItem("1", 1));
		a.add(new WeightItem("1", 1));
		a.add(new WeightItem("1", 1));
		a.add(new WeightItem("1", 1));
		a.add(new WeightItem("1", 1));
		a.add(new WeightItem("1", 1));
		a.add(new WeightItem("1", 1));
		a.add(new WeightItem("1", 1));
		a.add(new WeightItem("1", 1));
		a.add(new WeightItem("1", 0));
		a.add(new WeightItem("1", 0));
		a.add(new WeightItem("1", 0));
		a.add(new WeightItem("1", 0));
		a.add(new WeightItem("1", 0));
		a.add(new WeightItem("1", 0));
		a.add(new WeightItem("1", 0));
		a.add(new WeightItem("1", 0));
		a.add(new WeightItem("1", 0));
		a.add(new WeightItem("1", 0));
		a.add(new WeightItem("1", 0));
		
		System.out.println(buildBlocks(a));
	}

}

