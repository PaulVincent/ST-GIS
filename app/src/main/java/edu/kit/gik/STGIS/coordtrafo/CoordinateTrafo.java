package edu.kit.gik.STGIS.coordtrafo;

public class CoordinateTrafo {

	private DatumParams originCRF;
	private DatumParams targetCRF;
	private static final double RHO = 180. / Math.PI;

	double C0, C02, C03, C04, C05, C06, C07, C08;
	double L2, L3, L4, L5, L6, L7, L8;
	double T2, T3, T4, T5, T6;

	public CoordinateTrafo(int target) {

		originCRF = new DatumParams(DatumParams.GEO);
		targetCRF = new DatumParams(target);
	}

//	public static void main(String[] args) {
//		CoordinateTrafo ct = new CoordinateTrafo(4326);
//		double[] GK = ct.transformCoordinate(49, 8, 120);
//		System.out.println("rechtswert " + (float) GK[0] + "\n" + "hochswert " + (float) GK[1]);
//
//	}

	public double calcL0(double L) {
		double L0 = 0;

		L0 = Math.floor(L/3.) * 3;
		
		if (L%3. > 1.5) {
			L0 += 3;
		}
		
		return L0;
	}

	public double[] transformCoordinate(double B, double L, double h) {

		double Brad = B / RHO;// * Math.PI / 180.;
		double Lrad = L / RHO;// * Math.PI / 180.;
		double BradTarget;
		double LradTarget;

		if (originCRF.getEllipsoid() == targetCRF.getEllipsoid()) {
			System.out.println("gleiches SRS");
			BradTarget = Brad;
			LradTarget = Lrad;
		} else {
			
			double[] BLradTarget = datumSwitch(Brad, Lrad, h);
			BradTarget = BLradTarget[0];
			LradTarget = BLradTarget[1];
			System.out.println("NOT gleiches SRS");

		}
		double arclength = targetCRF.c
				* (targetCRF.E0 * BradTarget + targetCRF.E2
						* Math.sin(2 * BradTarget) + targetCRF.E4
						* Math.sin(4 * BradTarget) + targetCRF.E6
						* Math.sin(6 * BradTarget) + targetCRF.E8
						* Math.sin(8 * BradTarget) + targetCRF.E10
						* Math.sin(10 * BradTarget));
		double LTarget = LradTarget * RHO;
		double L0 = calcL0(LTarget);
		LradTarget = LradTarget - L0 / RHO;
		double t = Math.tan(BradTarget);
		C0 = Math.cos(BradTarget);
		C02 = C0 * C0;
		C03 = C02 * C0;
		C04 = C03 * C0;
		C05 = C04 * C0;
		C06 = C05 * C0;
		C07 = C06 * C0;
		C08 = C07 * C0;

		double NYQ = targetCRF.e_2 * C02;
		double V = Math.sqrt(1. + targetCRF.e_2 * C02);
		double N = targetCRF.c / V;
		L2 = LradTarget * LradTarget;
		L3 = L2 * LradTarget;
		L4 = L2 * L2;
		L5 = L2 * L3;
		L6 = L3 * L3;
		L7 = L3 * L4;
		L8 = L4 * L4;
		T2 = t * t;
		T3 = T2 * t;
		T4 = T2 * T2;
		T5 = T2 * T3;
		T6 = T3 * T3;

		double X = (arclength + 0.5 * t * N * C02 * L2)
				+ (1.0 / 24. * t * N * C04 * (5. - T2 + 9. * NYQ + 4. * NYQ * NYQ) * L4)
				+ (1.0 / 720. * t * N * C06 * (61. - 58.*T2 + T4 + 270. * NYQ - 330. * T2 * NYQ) * L6)
				+ (1.0 / 40320. * t * N * C08 * (1385. - 3111.*T2 + 543. * T4 - T6) * L8);
		double Y = N*C0*LradTarget
				+ 1./6. *N*C03*(1. - T2 + NYQ)*L3
				+ 1./120. *N*C05*(5. - 18.*T2 + T4 + 14.*NYQ - 58.*T2*NYQ)*L5
				+ 1./5040. *N*C07*(61. - 479. *T2 + 179.*T4 - T6)*L7;
		
		double east = Y*targetCRF.scale;
		double north = X*targetCRF.scale;
		
		if (targetCRF.getType() == DatumParams.GK) {
			east = east + L0/3. * 1000000 + 500000;
		} else if (targetCRF.getType() == DatumParams.UTM) {
			east = east + ((L0+3.)/6. + 30.) * 1000000 + 500000;
		}
		
		return new double[] { east, north };
	}

	private double[] datumSwitch(double Brad, double Lrad, double h) {
		// TODO Auto-generated method stub
		double dx = -598.1;
		double dy = -73.7;
		double dz = -418.2;
		double ex = 0.202 / (3600 * RHO);
		double ey = 0.045 / (3600 * RHO);
		double ez = -2.455 / (3600 * RHO);
		double m = -6.7 * 0.000001 + 1;

		// Change of geodaetic datum
		double origN = originCRF.a
				/ Math.sqrt(1 - originCRF.e2 * Math.pow(Math.sin(Brad), 2));
		double vecx = (origN + h) * Math.cos(Brad) * Math.cos(Lrad);
		double vecy = (origN + h) * Math.cos(Brad) * Math.sin(Lrad);
		;
		double vecz = (origN
				* (Math.pow(originCRF.b, 2) / Math.pow(originCRF.a, 2)) + h)
				* Math.sin(Brad);

		double rottedx = vecx * 1. + vecy * ez + vecz * -ey;
		double rottedy = vecx * -ez + vecy * 1. + vecz * ex;
		double rottedz = vecx * ey + vecy * -ex + vecz * 1.;
		double scalex = rottedx * m;
		double scaley = rottedy * m;
		double scalez = rottedz * m;
		double dxrot = dx * 1. + dy * ez + dz * -ey;
		double dyrot = dx * -ez + dy * 1. + dz * ex;
		double dzrot = dx * ey + dy * -ex + dz * 1.;

		double x = scalex + dxrot;
		double y = scaley + dyrot;
		double z = scalez + dzrot;

		double s = Math.sqrt(x * x + y * y);
		double T = Math.atan((z * targetCRF.a) / (s * targetCRF.b));
		double BradTarget = Math.atan((z + targetCRF.e2
				* (Math.pow(targetCRF.a, 2) / targetCRF.b)
				* Math.pow(Math.sin(T), 3))
				/ (s - targetCRF.e2 * targetCRF.a
						* Math.pow(Math.cos(T), 3)));
		double LradTarget = Math.atan(y / x);
		
		return new double[] {BradTarget, LradTarget};
	}
}
