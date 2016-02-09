package com.andredittrich.coordtrafo;

import java.util.Arrays;
import java.util.List;

public class DatumParams {

	// Coordinate Systems
	public static final int GEO = 4326;
	public static final int GK = 1;
	public static final int UTM = 2;
	// Ellipsoids
	public static final int BESSEL = 0;
	public static final int WGS84 = 1;
	public static final int GRS80 = 2;

	// public static final int EPSGCode;
	private static List<Integer> WGS84Codes = Arrays.asList(4326);
	private static List<Integer> ETRS89Codes = Arrays.asList(25828, 25829, 25830,
			25831, 25832, 25833, 25834, 25835, 25836, 25837, 25838);
	private static List<Integer> DHDNCodes = Arrays.asList(0, 4314, 31461, 31462, 31463,
			31464, 31465, 31466, 31467, 31468, 31469);
	
	//
	private int ellipsoid;
	private int type;
	public double a;
	public double b;
	public double e2; // 1.-Besselb/c;
	public double e_2; // -1. + (c/Besselb);
	public double c; // = Math.pow(a, 2)/b;
	public double scale;

	double E0, E2, E4, E6, E8, E10;

	public DatumParams(int epsg) {

		if (WGS84Codes.contains(epsg)) {
			System.out.println(epsg);
			ellipsoid = WGS84;
			setEllipsoidParams();
		} else if (ETRS89Codes.contains(epsg)) {
			System.out.println(epsg);
			ellipsoid = GRS80;
			setEllipsoidParams();
		} else if (DHDNCodes.contains(epsg)) {
			System.out.println(epsg);
			ellipsoid = BESSEL;
			setEllipsoidParams();
		} else {
			ellipsoid = BESSEL;
			System.out.println("MIST");
		}
		c = Math.pow(a, 2) / b;
		e2 = 1. - b / c;
		e_2 = c / b - 1.;
		E0 = (1. - Math.pow(e_2, 1) * (3. / 4.) + Math.pow(e_2, 2)
				* (45. / 64.) - Math.pow(e_2, 3) * (175. / 256.)
				+ Math.pow(e_2, 4) * (11025. / 16384.) - Math.pow(e_2, 5)
				* (43659. / 65536.));
		E2 = (-Math.pow(e_2, 1) * (3. / 8.) + Math.pow(e_2, 2) * (15. / 32.)
				- Math.pow(e_2, 3) * (525. / 1024.) + Math.pow(e_2, 4)
				* (2205. / 4096.) - Math.pow(e_2, 5) * (72765. / 131072.));
		E4 = (Math.pow(e_2, 2) * (15. / 256.) - Math.pow(e_2, 3)
				* (105. / 1024.) + Math.pow(e_2, 4) * (2205. / 16384.) - Math
				.pow(e_2, 5) * (10395. / 65536.));
		E6 = (-Math.pow(e_2, 3) * (35. / 3072.) + Math.pow(e_2, 4)
				* (315. / 12288.) - Math.pow(e_2, 5) * (31185. / 786432.));
		E8 = (Math.pow(e_2, 4) * (315. / 131072.) - Math.pow(e_2, 5)
				* (3465. / 524288.));
		E10 = (-Math.pow(e_2, 5) * (693. / 1310720.));
	}

	private void setEllipsoidParams() {
		switch (ellipsoid) {
		case WGS84:
			setType(UTM);
			a = 6378137.;
			b = 6356752.31425;
			scale = 0.9996;
			break;
		case BESSEL:
			setType(GK);
			a = 6377397.15508;
			b = 6356078.9629;
			scale = 1.0;
			break;
		case GRS80:
			setType(UTM);
			a = 6378137.;
			b = 6356752.31425;
			scale = 0.9996;
			break;
		default:
			setType(GK);
			a = 6377397.15508;
			b = 6356078.9629;
			scale = 1.0;
			break;
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getEllipsoid() {
		return ellipsoid;
	}

	public void setEllipsoid(int ellipsoid) {
		this.ellipsoid = ellipsoid;
	}
}