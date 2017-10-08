/*
 * Copyright (C) 2017 Alejandro Hernández Ferrero
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Funcionalidades relacionadas con el color.
 *
 * @author Alejandro
 */
public class ColorUtils {

    private static final double[] whitePoint = new double[]{95.0429, 100.0, 108.8900};

    /**
     * Matriz de conversión de RGB a XYZ.
     */
    private static final double[][] M = new double[][]{{0.4124, 0.3576, 0.1805},
                                                       {0.2126, 0.7152, 0.0722},
                                                       {0.0193, 0.1192, 0.9505}};
    /**
     * Matriz de conversión de XYZ a RGB.
     */
    public static final double[][] Mi = new double[][]{{3.2406, -1.5372, -0.4986},
                                                       {-0.9689, 1.8758, 0.0415},
                                                       {0.0557, -0.2040, 1.0570}};

    /**
     * Convierte un color de RGB a XYZ.
     *
     * @param R el componente rojo
     * @param G el componente verde
     * @param B el componente azul
     * @return el color en XYZ
     */
    public static double[] RGBtoXYZ(int R, int G, int B) {
        double[] result = new double[3];
        // Normaliza el rango a [0,1]
        double r = R / 255.0;
        double g = G / 255.0;
        double b = B / 255.0;
        if (r <= 0.04045) {
            r = r / 12.92;
        } else {
            r = Math.pow(((r + 0.055) / 1.055), 2.4);
        }
        if (g <= 0.04045) {
            g = g / 12.92;
        } else {
            g = Math.pow(((g + 0.055) / 1.055), 2.4);
        }
        if (b <= 0.04045) {
            b = b / 12.92;
        } else {
            b = Math.pow(((b + 0.055) / 1.055), 2.4);
        }
        r *= 100.0;
        g *= 100.0;
        b *= 100.0;
        // [X Y Z] = [r g b][M]
        result[0] = (r * M[0][0]) + (g * M[0][1]) + (b * M[0][2]);
        result[1] = (r * M[1][0]) + (g * M[1][1]) + (b * M[1][2]);
        result[2] = (r * M[2][0]) + (g * M[2][1]) + (b * M[2][2]);
        return result;
    }

    /**
     * Convierte un color de XYZ a LAB.
     *
     * @param X el componente X
     * @param Y el componente Y
     * @param Z el componente Z
     * @return el color en Lab
     */
    public static double[] XYZtoLAB(double X, double Y, double Z) {
        double x = X / whitePoint[0];
        double y = Y / whitePoint[1];
        double z = Z / whitePoint[2];
        if (x > 0.008856) {
            x = Math.pow(x, 1.0 / 3.0);
        } else {
            x = (7.787 * x) + (16.0 / 116.0);
        }
        if (y > 0.008856) {
            y = Math.pow(y, 1.0 / 3.0);
        } else {
            y = (7.787 * y) + (16.0 / 116.0);
        }
        if (z > 0.008856) {
            z = Math.pow(z, 1.0 / 3.0);
        } else {
            z = (7.787 * z) + (16.0 / 116.0);
        }
        double[] result = new double[3];
        result[0] = (116.0 * y) - 16.0;
        result[1] = 500.0 * (x - y);
        result[2] = 200.0 * (y - z);
        return result;
    }

    /**
     * Convierte un color de XYZ a LAB.
     *
     * @param XYZ el color en XYZ
     * @return el color en Lab
     */
    public static double[] XYZtoLAB(double[] XYZ) {
        return XYZtoLAB(XYZ[0], XYZ[1], XYZ[2]);
    }

    /**
     * Convierte un color de RGB a LAB.
     *
     * @param R el componente rojo
     * @param G el componente verde
     * @param B el componente azul
     * @return el color en Lab
     */
    public static double[] RGBtoLAB(int R, int G, int B) {
        return XYZtoLAB(RGBtoXYZ(R, G, B));
    }

    /**
     * Convierte un color de LAB a RGB.
     *
     * @param Lab el color en Lab
     * @return el color en rgb
     */
    public static int[] LABtoRGB(double[] Lab) {
        return XYZtoRGB(LABtoXYZ(Lab));
    }

    /**
     * Convierte un color de LAB a XYZ.
     *
     * @param L el componente L
     * @param a el componente a
     * @param b el componente b
     * @return XYZ values
     */
    public static double[] LABtoXYZ(double L, double a, double b) {
        double[] result = new double[3];
        double y = (L + 16.0) / 116.0;
        double y3 = Math.pow(y, 3.0);
        double x = (a / 500.0) + y;
        double x3 = Math.pow(x, 3.0);
        double z = y - (b / 200.0);
        double z3 = Math.pow(z, 3.0);
        if (y3 > 0.008856) {
            y = y3;
        } else {
            y = (y - (16.0 / 116.0)) / 7.787;
        }
        if (x3 > 0.008856) {
            x = x3;
        } else {
            x = (x - (16.0 / 116.0)) / 7.787;
        }
        if (z3 > 0.008856) {
            z = z3;
        } else {
            z = (z - (16.0 / 116.0)) / 7.787;
        }
        result[0] = x * whitePoint[0];
        result[1] = y * whitePoint[1];
        result[2] = z * whitePoint[2];
        return result;
    }

    /**
     * Convierte un color de LAB a XYZ.
     *
     * @param Lab el color en Lab
     * @return XYZ values el color en XYZ
     */
    public static double[] LABtoXYZ(double[] Lab) {
        return LABtoXYZ(Lab[0], Lab[1], Lab[2]);
    }

    /**
     * Convierte un color de XYZ a RGB.
     *
     * @param X el componente X
     * @param Y el componente Y
     * @param Z el componente Z
     * @return el color en RGB
     */
    public static int[] XYZtoRGB(double X, double Y, double Z) {
        int[] result = new int[3];
        double x = X / 100.0;
        double y = Y / 100.0;
        double z = Z / 100.0;
        // [r g b] = [X Y Z][Mi]
        double r = (x * Mi[0][0]) + (y * Mi[0][1]) + (z * Mi[0][2]);
        double g = (x * Mi[1][0]) + (y * Mi[1][1]) + (z * Mi[1][2]);
        double b = (x * Mi[2][0]) + (y * Mi[2][1]) + (z * Mi[2][2]);
        if (r > 0.0031308) {
            r = ((1.055 * Math.pow(r, 1.0 / 2.4)) - 0.055);
        } else {
            r = (r * 12.92);
        }
        if (g > 0.0031308) {
            g = ((1.055 * Math.pow(g, 1.0 / 2.4)) - 0.055);
        } else {
            g = (g * 12.92);
        }
        if (b > 0.0031308) {
            b = ((1.055 * Math.pow(b, 1.0 / 2.4)) - 0.055);
        } else {
            b = (b * 12.92);
        }
        r = (r < 0) ? 0 : r;
        g = (g < 0) ? 0 : g;
        b = (b < 0) ? 0 : b;
        // Lo convierte a [0,255]
        result[0] = (int) Math.round(r * 255);
        result[1] = (int) Math.round(g * 255);
        result[2] = (int) Math.round(b * 255);
        return result;
    }

    /**
     * Convierte un color de XYZ a RGB.
     *
     * @param XYZ el color en XYZ
     * @return el color en RGB
     */
    public static int[] XYZtoRGB(double[] XYZ) {
        return XYZtoRGB(XYZ[0], XYZ[1], XYZ[2]);
    }

    /**
     * Convierte un color de LAB a RGB.
     *
     * @param L el componente L
     * @param a el componente a
     * @param b el componente b
     * @return el color en RGB
     */
    public static int[] LABtoRGB(double L, double a, double b) {
        return XYZtoRGB(LABtoXYZ(L, a, b));
    }

    /**
     * Calcula la diferencia entre dos colores en el espacio de color Lab
     * mediante la fórmula Delta E 76.
     *
     * @param Lab1 el primer color
     * @param Lab2 el segundo color
     * @return la diferencia
     */
    public static double deltaE76(double[] Lab1, double[] Lab2) {
        double L1 = Lab1[0];
        double a1 = Lab1[1];
        double b1 = Lab1[2];
        double L2 = Lab2[0];
        double a2 = Lab2[1];
        double b2 = Lab2[2];
        double deltaL = L2 - L1;
        double deltaA = a2 - a1;
        double deltaB = b2 - b1;
        return Math.sqrt(deltaL * deltaL + deltaA * deltaA + deltaB * deltaB);
    }

    /**
     * Calcula la diferencia entre dos colores en el espacio de color Lab
     * mediante la fórmula Delta E 2000.
     *
     * @param Lab1 el primer color
     * @param Lab2 el segundo color
     * @return la diferencia
     */
    public static double deltaE00(double[] Lab1, double[] Lab2) {
        double L1 = Lab1[0];
        double a1 = Lab1[1];
        double b1 = Lab1[2];
        double L2 = Lab2[0];
        double a2 = Lab2[1];
        double b2 = Lab2[2];
        // Pesos
        final double kl = 1.0;
        final double kc = 1.0;
        final double kh = 1.0;

        double deltaLp = L2 - L1;
        double Lb = (L1 + L2) / 2;
        double C1 = Math.sqrt(a1 * a1 + b1 * b1);
        double C2 = Math.sqrt(a2 * a2 + b2 * b2);
        double Cb = (C1 + C2) / 2.0;
        double G = 0.5 * (1 - Math.sqrt(Math.pow(Cb, 7) / (Math.pow(Cb, 7) + 6103515625l)));
        double ap1 = a1 + a1 * G;
        double ap2 = a2 + a2 * G;
        double Cp1 = Math.sqrt(ap1 * ap1 + b1 * b1);
        double Cp2 = Math.sqrt(ap2 * ap2 + b2 * b2);
        double Cbp = (Cp1 + Cp2) / 2.0;
        double deltaCp = Cp2 - Cp1;
        double hp1 = (Math.toDegrees(Math.atan2(b1, ap1)) + 360) % 360.0;
        double hp2 = (Math.toDegrees(Math.atan2(b2, ap2)) + 360) % 360.0;
        double dh = Math.abs(hp1 - hp2);
        double deltahp;
        if (Cp1 * Cp2 == 0) {
            deltahp = 0;
        } else {
            if (dh <= 180) {
                deltahp = hp2 - hp1;
            } else if (hp2 <= hp1) {
                deltahp = hp2 - hp1 + 360;
            } else {
                deltahp = hp2 - hp1 - 360;
            }
        }
        double deltaHp = 2 * Math.sqrt(Cp1 * Cp2) * Math.sin(Math.toRadians(deltahp / 2));
        double Hbp;
        if (Cp1 * Cp2 == 0) {
            Hbp = hp1 + hp2;
        } else {
            if (dh <= 180) {
                Hbp = (hp1 + hp2) / 2;
            } else if (hp1 + hp2 < 360) {
                Hbp = (hp1 + hp2 + 360) / 2;
            } else {
                Hbp = (hp1 + hp2 - 360) / 2;
            }
        }
        double T = 1 - 0.17 * Math.cos(Math.toRadians(Hbp - 30))
                   + 0.24 * Math.cos(Math.toRadians(2 * Hbp))
                   + 0.32 * Math.cos(Math.toRadians(3 * Hbp + 6))
                   - 0.20 * Math.cos(Math.toRadians(4 * Hbp - 63));
        double Sl = 1 + ((0.015 * ((Lb - 50) * (Lb - 50))) / (Math.sqrt(20 + ((Lb - 50) * (Lb - 50)))));
        double Sc = 1 + 0.045 * Cbp;
        double Sh = 1 + 0.015 * Cbp * T;
        double Rt = -2 * Math.sqrt(Math.pow(Cbp, 7) / (Math.pow(Cbp, 7) + 6103515625l))
                    * Math.sin(Math.toRadians(60 * Math.exp(-(((Hbp - 275) / 25) * ((Hbp - 275) / 25)))));

        double wL = deltaLp / (kl * Sl);
        double wC = deltaCp / (kc * Sc);
        double wH = deltaHp / (kh * Sh);

        return Math.sqrt(wL * wL + wC * wC + wH * wH + Rt * wC * wH);
    }

    /**
     * Encapsula un color con nombre.
     */
    public static class ColorName extends Color {

        private final String name;
        private final double[] lab;

        /**
         * Crea un nuevo color opaco con nombre.
         *
         * @param name el nombre
         * @param r el componente rojo
         * @param g el componente verde
         * @param b el componente
         */
        public ColorName(String name, int r, int g, int b) {
            super(r, g, b);
            this.name = name;
            lab = RGBtoLAB(r, g, b);
        }

        /**
         * Crea un nuevo color cuyo nombre es identificado automaticamente.
         *
         * @param r el componente rojo
         * @param g el componente verde
         * @param b el componente azul
         * @param a el componente alfa
         */
        public ColorName(int r, int g, int b, int a) {
            super(r, g, b, a);
            lab = RGBtoLAB(r, g, b);
            this.name = getColorNameFromLab(lab);
        }

        /**
         * Crea un nuevo color cuyo nombre es identificado automaticamente.
         *
         * @param rgb el color en rgb
         */
        public ColorName(int[] rgb) {
            this(rgb[0], rgb[1], rgb[2], rgb.length > 3 ? rgb[3] : 1);
        }

        /**
         * Crea un nuevo color cuyo nombre es identificado automaticamente.
         *
         * @param rgb un entero con los valores rgb combinados
         */
        public ColorName(int rgb) {
            super(rgb);
            lab = RGBtoLAB(getRed(), getGreen(), getBlue());
            this.name = getColorNameFromLab(lab);
        }

        /**
         * Crea un nuevo color a partir del dado e identifica su nombre.
         *
         * @param color el color
         */
        public ColorName(Color color) {
            this(color.getRGB());
        }

        /**
         * Obtiene su valor en el espacio de color Lab.
         *
         * @return el color en Lab
         */
        public double[] getLab() {
            return lab;
        }

        /**
         * Calcula el error cuadrático medio entre un color y éste.
         *
         * @param r el componente rojo
         * @param g el componente verde
         * @param b el componente azul
         * @return el error cuadrático medio
         */
        public int computeMSE(int r, int g, int b) {
            return (int) (((r - getRed()) * (r - getRed()) + (g - getGreen()) * (g - getGreen()) + (b - getBlue()) * (b - getBlue())) / 3);
        }

        /**
         * Obtiene el nombre del color.
         *
         * @return el nombre del color
         */
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        /**
         * Identifica el nombre que mejor describe a un color mediante sus
         * componentes en Lab.
         * <p>
         * Compara el color con una lista de colores estándar y obtiene el
         * nombre del que mayor semejanza ofrezca. Para esta comparación se usa
         * el espacio de color Lab, que tiene la característica de ser
         * perceptivamente lineal, es decir, un cambio de la misma cantidad en
         * un valor produce un cambio casi de la misma importancia visual, por
         * lo que el color identificado será el más próximo visualmente.
         *
         * @param Lab el color en lab
         * @return el nombre
         */
        public static String getColorNameFromLab(double[] Lab) {
            initColorList();
            ColorName closestMatch = null;
            double min = Double.MAX_VALUE;
            double dist;
            for (ColorName color : colorList) {
                dist = deltaE00(Lab, color.getLab());
                if (dist < min) {
                    min = dist;
                    closestMatch = color;
                }
            }
            if (closestMatch != null) {
                return closestMatch.getName();
            } else {
                return "Unknown";
            }
        }

        /**
         * Identifica el nombre que mejor describe a un color definido en rgb.
         * <p>
         * Compara el color con una lista de colores estándar y obtiene el
         * nombre del que mayor semejanza ofrezca. Para esta comparación se usa
         * el espacio de color rgb, por lo que el color identificado será el más
         * próximo en cuanto a sus valores rgb.
         *
         * @param r el componente rojo
         * @param g el componente verde
         * @param b el componente azul
         * @return el nombre
         */
        public static String getColorNameFromRgb(int r, int g, int b) {
            initColorList();
            ColorName closestMatch = null;
            int minMSE = Integer.MAX_VALUE;
            int mse;
            for (ColorName c : colorList) {
                mse = c.computeMSE(r, g, b);
                if (mse < minMSE) {
                    minMSE = mse;
                    closestMatch = c;
                }
            }

            if (closestMatch != null) {
                return closestMatch.getName();
            } else {
                return "Unknown";
            }
        }

        /**
         * Obtiene el color opuesto al dado.
         *
         * @param color el color dado
         * @return el color opuesto
         */
        public static ColorName getOpposite(java.awt.Color color) {
            return new ColorName((~color.getRed()) & 0xff, (~color.getGreen()) & 0xff, (~color.getBlue()) & 0xff, color.getAlpha());
        }

        /**
         * Inicializa perezosamente la lista de colores.
         */
        private static void initColorList() {
            if (colorList == null) {
                colorList = new ArrayList<>();
                // Rellena la lista con los colores estándar mediante reflexión
                Field[] fields = ColorName.class.getDeclaredFields();
                for (Field field : fields) {
                    try {
                        if (field.getType().isAssignableFrom(ColorName.class)) {
                            ColorName color = (ColorName) field.get(null);
                            colorList.add(color);
                        }
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        Logger.getLogger(ColorUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        private static ArrayList<ColorName> colorList;

        public static final ColorName AliceBlue = new ColorName("AliceBlue", 0xF0, 0xF8, 0xFF);
        public static final ColorName AntiqueWhite = new ColorName("AntiqueWhite", 0xFA, 0xEB, 0xD7);
        public static final ColorName Aqua = new ColorName("Aqua", 0x00, 0xFF, 0xFF);
        public static final ColorName Aquamarine = new ColorName("Aquamarine", 0x7F, 0xFF, 0xD4);
        public static final ColorName Azure = new ColorName("Azure", 0xF0, 0xFF, 0xFF);
        public static final ColorName Beige = new ColorName("Beige", 0xF5, 0xF5, 0xDC);
        public static final ColorName Bisque = new ColorName("Bisque", 0xFF, 0xE4, 0xC4);
        public static final ColorName Black = new ColorName("Black", 0x00, 0x00, 0x00);
        public static final ColorName BlanchedAlmond = new ColorName("BlanchedAlmond", 0xFF, 0xEB, 0xCD);
        public static final ColorName Blue = new ColorName("Blue", 0x00, 0x00, 0xFF);
        public static final ColorName BlueViolet = new ColorName("BlueViolet", 0x8A, 0x2B, 0xE2);
        public static final ColorName Brown = new ColorName("Brown", 0xA5, 0x2A, 0x2A);
        public static final ColorName BurlyWood = new ColorName("BurlyWood", 0xDE, 0xB8, 0x87);
        public static final ColorName CadetBlue = new ColorName("CadetBlue", 0x5F, 0x9E, 0xA0);
        public static final ColorName Chartreuse = new ColorName("Chartreuse", 0x7F, 0xFF, 0x00);
        public static final ColorName Chocolate = new ColorName("Chocolate", 0xD2, 0x69, 0x1E);
        public static final ColorName Coral = new ColorName("Coral", 0xFF, 0x7F, 0x50);
        public static final ColorName CornflowerBlue = new ColorName("CornflowerBlue", 0x64, 0x95, 0xED);
        public static final ColorName Cornsilk = new ColorName("Cornsilk", 0xFF, 0xF8, 0xDC);
        public static final ColorName Crimson = new ColorName("Crimson", 0xDC, 0x14, 0x3C);
        public static final ColorName Cyan = new ColorName("Cyan", 0x00, 0xFF, 0xFF);
        public static final ColorName DarkBlue = new ColorName("DarkBlue", 0x00, 0x00, 0x8B);
        public static final ColorName DarkCyan = new ColorName("DarkCyan", 0x00, 0x8B, 0x8B);
        public static final ColorName DarkGoldenRod = new ColorName("DarkGoldenRod", 0xB8, 0x86, 0x0B);
        public static final ColorName DarkGray = new ColorName("DarkGray", 0xA9, 0xA9, 0xA9);
        public static final ColorName DarkGreen = new ColorName("DarkGreen", 0x00, 0x64, 0x00);
        public static final ColorName DarkKhaki = new ColorName("DarkKhaki", 0xBD, 0xB7, 0x6B);
        public static final ColorName DarkMagenta = new ColorName("DarkMagenta", 0x8B, 0x00, 0x8B);
        public static final ColorName DarkOliveGreen = new ColorName("DarkOliveGreen", 0x55, 0x6B, 0x2F);
        public static final ColorName DarkOrange = new ColorName("DarkOrange", 0xFF, 0x8C, 0x00);
        public static final ColorName DarkOrchid = new ColorName("DarkOrchid", 0x99, 0x32, 0xCC);
        public static final ColorName DarkRed = new ColorName("DarkRed", 0x8B, 0x00, 0x00);
        public static final ColorName DarkSalmon = new ColorName("DarkSalmon", 0xE9, 0x96, 0x7A);
        public static final ColorName DarkSeaGreen = new ColorName("DarkSeaGreen", 0x8F, 0xBC, 0x8F);
        public static final ColorName DarkSlateBlue = new ColorName("DarkSlateBlue", 0x48, 0x3D, 0x8B);
        public static final ColorName DarkSlateGray = new ColorName("DarkSlateGray", 0x2F, 0x4F, 0x4F);
        public static final ColorName DarkTurquoise = new ColorName("DarkTurquoise", 0x00, 0xCE, 0xD1);
        public static final ColorName DarkViolet = new ColorName("DarkViolet", 0x94, 0x00, 0xD3);
        public static final ColorName DeepPink = new ColorName("DeepPink", 0xFF, 0x14, 0x93);
        public static final ColorName DeepSkyBlue = new ColorName("DeepSkyBlue", 0x00, 0xBF, 0xFF);
        public static final ColorName DimGray = new ColorName("DimGray", 0x69, 0x69, 0x69);
        public static final ColorName DodgerBlue = new ColorName("DodgerBlue", 0x1E, 0x90, 0xFF);
        public static final ColorName FireBrick = new ColorName("FireBrick", 0xB2, 0x22, 0x22);
        public static final ColorName FloralWhite = new ColorName("FloralWhite", 0xFF, 0xFA, 0xF0);
        public static final ColorName ForestGreen = new ColorName("ForestGreen", 0x22, 0x8B, 0x22);
        public static final ColorName Fuchsia = new ColorName("Fuchsia", 0xFF, 0x00, 0xFF);
        public static final ColorName Gainsboro = new ColorName("Gainsboro", 0xDC, 0xDC, 0xDC);
        public static final ColorName GhostWhite = new ColorName("GhostWhite", 0xF8, 0xF8, 0xFF);
        public static final ColorName Gold = new ColorName("Gold", 0xFF, 0xD7, 0x00);
        public static final ColorName GoldenRod = new ColorName("GoldenRod", 0xDA, 0xA5, 0x20);
        public static final ColorName Gray = new ColorName("Gray", 0x80, 0x80, 0x80);
        public static final ColorName Green = new ColorName("Green", 0x00, 0x80, 0x00);
        public static final ColorName GreenYellow = new ColorName("GreenYellow", 0xAD, 0xFF, 0x2F);
        public static final ColorName HoneyDew = new ColorName("HoneyDew", 0xF0, 0xFF, 0xF0);
        public static final ColorName HotPink = new ColorName("HotPink", 0xFF, 0x69, 0xB4);
        public static final ColorName IndianRed = new ColorName("IndianRed", 0xCD, 0x5C, 0x5C);
        public static final ColorName Indigo = new ColorName("Indigo", 0x4B, 0x00, 0x82);
        public static final ColorName Ivory = new ColorName("Ivory", 0xFF, 0xFF, 0xF0);
        public static final ColorName Khaki = new ColorName("Khaki", 0xF0, 0xE6, 0x8C);
        public static final ColorName Lavender = new ColorName("Lavender", 0xE6, 0xE6, 0xFA);
        public static final ColorName LavenderBlush = new ColorName("LavenderBlush", 0xFF, 0xF0, 0xF5);
        public static final ColorName LawnGreen = new ColorName("LawnGreen", 0x7C, 0xFC, 0x00);
        public static final ColorName LemonChiffon = new ColorName("LemonChiffon", 0xFF, 0xFA, 0xCD);
        public static final ColorName LightBlue = new ColorName("LightBlue", 0xAD, 0xD8, 0xE6);
        public static final ColorName LightCoral = new ColorName("LightCoral", 0xF0, 0x80, 0x80);
        public static final ColorName LightCyan = new ColorName("LightCyan", 0xE0, 0xFF, 0xFF);
        public static final ColorName LightGoldenRodYellow = new ColorName("LightGoldenRodYellow", 0xFA, 0xFA, 0xD2);
        public static final ColorName LightGray = new ColorName("LightGray", 0xD3, 0xD3, 0xD3);
        public static final ColorName LightGreen = new ColorName("LightGreen", 0x90, 0xEE, 0x90);
        public static final ColorName LightPink = new ColorName("LightPink", 0xFF, 0xB6, 0xC1);
        public static final ColorName LightSalmon = new ColorName("LightSalmon", 0xFF, 0xA0, 0x7A);
        public static final ColorName LightSeaGreen = new ColorName("LightSeaGreen", 0x20, 0xB2, 0xAA);
        public static final ColorName LightSkyBlue = new ColorName("LightSkyBlue", 0x87, 0xCE, 0xFA);
        public static final ColorName LightSlateGray = new ColorName("LightSlateGray", 0x77, 0x88, 0x99);
        public static final ColorName LightSteelBlue = new ColorName("LightSteelBlue", 0xB0, 0xC4, 0xDE);
        public static final ColorName LightYellow = new ColorName("LightYellow", 0xFF, 0xFF, 0xE0);
        public static final ColorName Lime = new ColorName("Lime", 0x00, 0xFF, 0x00);
        public static final ColorName LimeGreen = new ColorName("LimeGreen", 0x32, 0xCD, 0x32);
        public static final ColorName Linen = new ColorName("Linen", 0xFA, 0xF0, 0xE6);
        public static final ColorName Magenta = new ColorName("Magenta", 0xFF, 0x00, 0xFF);
        public static final ColorName Maroon = new ColorName("Maroon", 0x80, 0x00, 0x00);
        public static final ColorName MediumAquaMarine = new ColorName("MediumAquaMarine", 0x66, 0xCD, 0xAA);
        public static final ColorName MediumBlue = new ColorName("MediumBlue", 0x00, 0x00, 0xCD);
        public static final ColorName MediumOrchid = new ColorName("MediumOrchid", 0xBA, 0x55, 0xD3);
        public static final ColorName MediumPurple = new ColorName("MediumPurple", 0x93, 0x70, 0xDB);
        public static final ColorName MediumSeaGreen = new ColorName("MediumSeaGreen", 0x3C, 0xB3, 0x71);
        public static final ColorName MediumSlateBlue = new ColorName("MediumSlateBlue", 0x7B, 0x68, 0xEE);
        public static final ColorName MediumSpringGreen = new ColorName("MediumSpringGreen", 0x00, 0xFA, 0x9A);
        public static final ColorName MediumTurquoise = new ColorName("MediumTurquoise", 0x48, 0xD1, 0xCC);
        public static final ColorName MediumVioletRed = new ColorName("MediumVioletRed", 0xC7, 0x15, 0x85);
        public static final ColorName MidnightBlue = new ColorName("MidnightBlue", 0x19, 0x19, 0x70);
        public static final ColorName MintCream = new ColorName("MintCream", 0xF5, 0xFF, 0xFA);
        public static final ColorName MistyRose = new ColorName("MistyRose", 0xFF, 0xE4, 0xE1);
        public static final ColorName Moccasin = new ColorName("Moccasin", 0xFF, 0xE4, 0xB5);
        public static final ColorName NavajoWhite = new ColorName("NavajoWhite", 0xFF, 0xDE, 0xAD);
        public static final ColorName Navy = new ColorName("Navy", 0x00, 0x00, 0x80);
        public static final ColorName OldLace = new ColorName("OldLace", 0xFD, 0xF5, 0xE6);
        public static final ColorName Olive = new ColorName("Olive", 0x80, 0x80, 0x00);
        public static final ColorName OliveDrab = new ColorName("OliveDrab", 0x6B, 0x8E, 0x23);
        public static final ColorName Orange = new ColorName("Orange", 0xFF, 0xA5, 0x00);
        public static final ColorName OrangeRed = new ColorName("OrangeRed", 0xFF, 0x45, 0x00);
        public static final ColorName Orchid = new ColorName("Orchid", 0xDA, 0x70, 0xD6);
        public static final ColorName PaleGoldenRod = new ColorName("PaleGoldenRod", 0xEE, 0xE8, 0xAA);
        public static final ColorName PaleGreen = new ColorName("PaleGreen", 0x98, 0xFB, 0x98);
        public static final ColorName PaleTurquoise = new ColorName("PaleTurquoise", 0xAF, 0xEE, 0xEE);
        public static final ColorName PaleVioletRed = new ColorName("PaleVioletRed", 0xDB, 0x70, 0x93);
        public static final ColorName PapayaWhip = new ColorName("PapayaWhip", 0xFF, 0xEF, 0xD5);
        public static final ColorName PeachPuff = new ColorName("PeachPuff", 0xFF, 0xDA, 0xB9);
        public static final ColorName Peru = new ColorName("Peru", 0xCD, 0x85, 0x3F);
        public static final ColorName Pink = new ColorName("Pink", 0xFF, 0xC0, 0xCB);
        public static final ColorName Plum = new ColorName("Plum", 0xDD, 0xA0, 0xDD);
        public static final ColorName PowderBlue = new ColorName("PowderBlue", 0xB0, 0xE0, 0xE6);
        public static final ColorName Purple = new ColorName("Purple", 0x80, 0x00, 0x80);
        public static final ColorName Red = new ColorName("Red", 0xFF, 0x00, 0x00);
        public static final ColorName RosyBrown = new ColorName("RosyBrown", 0xBC, 0x8F, 0x8F);
        public static final ColorName RoyalBlue = new ColorName("RoyalBlue", 0x41, 0x69, 0xE1);
        public static final ColorName SaddleBrown = new ColorName("SaddleBrown", 0x8B, 0x45, 0x13);
        public static final ColorName Salmon = new ColorName("Salmon", 0xFA, 0x80, 0x72);
        public static final ColorName SandyBrown = new ColorName("SandyBrown", 0xF4, 0xA4, 0x60);
        public static final ColorName SeaGreen = new ColorName("SeaGreen", 0x2E, 0x8B, 0x57);
        public static final ColorName SeaShell = new ColorName("SeaShell", 0xFF, 0xF5, 0xEE);
        public static final ColorName Sienna = new ColorName("Sienna", 0xA0, 0x52, 0x2D);
        public static final ColorName Silver = new ColorName("Silver", 0xC0, 0xC0, 0xC0);
        public static final ColorName SkyBlue = new ColorName("SkyBlue", 0x87, 0xCE, 0xEB);
        public static final ColorName SlateBlue = new ColorName("SlateBlue", 0x6A, 0x5A, 0xCD);
        public static final ColorName SlateGray = new ColorName("SlateGray", 0x70, 0x80, 0x90);
        public static final ColorName Snow = new ColorName("Snow", 0xFF, 0xFA, 0xFA);
        public static final ColorName SpringGreen = new ColorName("SpringGreen", 0x00, 0xFF, 0x7F);
        public static final ColorName SteelBlue = new ColorName("SteelBlue", 0x46, 0x82, 0xB4);
        public static final ColorName Tan = new ColorName("Tan", 0xD2, 0xB4, 0x8C);
        public static final ColorName Teal = new ColorName("Teal", 0x00, 0x80, 0x80);
        public static final ColorName Thistle = new ColorName("Thistle", 0xD8, 0xBF, 0xD8);
        public static final ColorName Tomato = new ColorName("Tomato", 0xFF, 0x63, 0x47);
        public static final ColorName Turquoise = new ColorName("Turquoise", 0x40, 0xE0, 0xD0);
        public static final ColorName Violet = new ColorName("Violet", 0xEE, 0x82, 0xEE);
        public static final ColorName Wheat = new ColorName("Wheat", 0xF5, 0xDE, 0xB3);
        public static final ColorName White = new ColorName("White", 0xFF, 0xFF, 0xFF);
        public static final ColorName WhiteSmoke = new ColorName("WhiteSmoke", 0xF5, 0xF5, 0xF5);
        public static final ColorName Yellow = new ColorName("Yellow", 0xFF, 0xFF, 0x00);
        public static final ColorName YellowGreen = new ColorName("YellowGreen", 0x9A, 0xCD, 0x32);

    }

    /**
     * Editor de datos del tipo {@link ColorName} en una tabla.
     */
    public static class ColorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

        private final JPanel cmp;
        private JButton button;
        private JColorChooser colorChooser;
        private JDialog dialog;
        private ColorName color;

        /**
         * Crea un nuevo editor.
         */
        public ColorEditor() {
            colorChooser = new JColorChooser();
            dialog = JColorChooser.createDialog(button, "Pick a Color", true, colorChooser, this, null);
            button = new JButton();
            // Dentro de un JPanel para que tenga el borde como la tabla
            cmp = new JPanel(new BorderLayout());
            cmp.add(button, BorderLayout.CENTER);
            button.setBorderPainted(false);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Muestra el diálogo para elegir color
                    colorChooser.setColor(color);
                    dialog.setVisible(true);
                    fireEditingStopped();
                }
            });
        }

        /**
         * Cuando se pulsa Ok en el diálogo para elegir color se obtiene el
         * color seleccionado.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            color = new ColorName(colorChooser.getColor());
        }

        @Override
        public Object getCellEditorValue() {
            return color;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            color = (ColorName) value;
            button.setBackground(color);
            cmp.setBackground(table.getSelectionBackground());
            cmp.setToolTipText(color.toString());
            return cmp;
        }
    }

    /**
     * {@link TableCellRenderer} para datos del tipo {@link ColorName}.
     */
    public static class ColorRenderer extends JPanel implements TableCellRenderer {

        private final JButton button;

        public ColorRenderer() {
            setLayout(new BorderLayout());
            button = new JButton();
            add(button, BorderLayout.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {
            ColorName newColor = (ColorName) color;
            button.setBackground(newColor);
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getSelectionForeground());
            }
            setToolTipText(newColor.toString());
            return this;
        }
    }
}
