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

/**
 * Funcionalidades relacionadas con la aleatoriedad.
 *
 * @author Alejandro Hernández Ferrero
 */
public class RandomUtils {

    private static final java.util.Random random = new java.util.Random();

    /**
     * Obtiene un entero aleatorio entre 0 y el valor especificado.
     *
     * @param max el límite del rango
     * @return un entero aleatorio en el rango <code>[0,max)</code>
     */
    public static int random(int max) {
        return random.nextInt(max);
    }

    /**
     * Obtiene un entero aleatorio en el rango especificado.
     *
     * @param min el límite inferior
     * @param max el límite superior
     * @return un entero aleatorio en el rango <code>[min,max)</code>
     */
    public static int random(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    /**
     * Obtiene un número aleatorio entre 0 y el valor especificado.
     *
     * @param max el límite del rango
     * @return un número aleatorio en el rango <code>[0,max)</code>
     */
    public static float random(float max) {
        return random.nextFloat() * max;
    }

    /**
     * Obtiene un número aleatorio en el rango especificado.
     *
     * @param min el límite inferior
     * @param max el límite superior
     * @return un número aleatorio en el rango <code>[min,max)</code>
     */
    public static float random(float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }

    /**
     * Simula el lanzamiento de un dado con 2 resultados posibles pero con
     * distintas probabilidades.
     *
     * @param probability la probabilidad de que salga <code>true</code>
     * @return <code>true</code>/<code>false</code> aleatoriamente
     */
    public static boolean roll(float probability) {
        return random(1f) < probability;
    }

    /**
     * Simula el lanzamiento de un dado con 2 resultados posibles.
     *
     * @return <code>true</code>/<code>false</code> aleatoriamente
     */
    public static boolean roll() {
        return roll(0.5f);
    }

    /**
     * Obtiene un número aleatorio con una distribución normal.
     *
     * @param mean la media
     * @param stdDev la desviación estándar
     * @param min el mínimo
     * @param max el máximo
     * @return un número aleatorio
     */
    public static float gaussian(float mean, float stdDev, float min, float max) {
        float r;
        do {
            r = (float) (random.nextGaussian() * stdDev + mean);
        } while (r < min || r > max);
        return r;
    }

    /**
     * Generador de números aleatorios con igual probabilidad dentro de un
     * rango.
     */
    public static class Random {

        private float min;
        private float max;

        /**
         * Crea un nuevo generador.
         *
         * @param min el límite inferior
         * @param max el límite superior
         */
        public Random(float min, float max) {
            this.min = min;
            this.max = max;
        }

        /**
         * Crea un nuevo generador como copia independiente de otro.
         *
         * @param copy el generador al que copiar
         */
        public Random(Random copy) {
            this.min = copy.getMin();
            this.max = copy.getMax();
        }

        /**
         * Obtiene una copia independiente de este generador.
         *
         * @return una copia
         */
        public Random copy() {
            return new Random(this);
        }

        /**
         * Obtiene un nuevo número generado.
         *
         * @return el número aleatorio generado
         */
        public float generate() {
            return random(min, max);
        }

        /**
         * Obtiene el límite inferior.
         *
         * @return el límite inferior
         */
        public float getMin() {
            return min;
        }

        /**
         * Establece el límite inferior.
         *
         * @param min el límite inferior
         */
        public void setMin(float min) {
            this.min = min;
        }

        /**
         * Obtiene el límite superior.
         *
         * @return el límite inferior
         */
        public float getMax() {
            return max;
        }

        /**
         * Establece el límite inferior.
         *
         * @param max el límite superior
         */
        public void setMax(float max) {
            this.max = max;
        }
    }

    /**
     * Generador de números aleatorios con una distribución normal dentro de un
     * rango.
     */
    public static class RandomGaussian {

        private float mean;
        private float stdDev;
        private float min;
        private float max;

        /**
         * Crea un nuevo generador.
         *
         * @param mean la media
         * @param stdDev la desviación estándar
         * @param min el mínimo
         * @param max el máximo
         */
        public RandomGaussian(float mean, float stdDev, float min, float max) {
            this.mean = mean;
            this.stdDev = stdDev;
            this.min = min;
            this.max = max;
        }

        /**
         * Crea un nuevo generador como copia independiente de otro.
         *
         * @param copy el generador al que copiar
         */
        public RandomGaussian(RandomGaussian copy) {
            this.mean = copy.getMean();
            this.stdDev = copy.getStdDev();
            this.min = copy.getMin();
            this.max = copy.getMax();
        }

        /**
         * Obtiene una copia independiente de este generador.
         *
         * @return una copia
         */
        public RandomGaussian copy() {
            return new RandomGaussian(this);
        }

        /**
         * Obtiene un nuevo número generado.
         *
         * @return el número aleatorio generado
         */
        public float generate() {
            return gaussian(mean, stdDev, min, max);
        }

        /**
         * Obtiene la media.
         *
         * @return la media
         */
        public float getMean() {
            return mean;
        }

        /**
         * Establece la media.
         *
         * @param mean la media
         */
        public void setMean(float mean) {
            this.mean = mean;
        }

        /**
         * Obtiene la desviación estándar.
         *
         * @return la desviación estándar
         */
        public float getStdDev() {
            return stdDev;
        }

        /**
         * Establece la desviación estándar.
         *
         * @param stdDev la desviación estándar
         */
        public void setStdDev(float stdDev) {
            this.stdDev = stdDev;
        }

        /**
         * Obtiene el límite inferior.
         *
         * @return el límite inferior
         */
        public float getMin() {
            return min;
        }

        /**
         * Establece el límite inferior.
         *
         * @param min el límite inferior
         */
        public void setMin(float min) {
            this.min = min;
        }

        /**
         * Obtiene el límite superior.
         *
         * @return el límite inferior
         */
        public float getMax() {
            return max;
        }

        /**
         * Establece el límite inferior.
         *
         * @param max el límite superior
         */
        public void setMax(float max) {
            this.max = max;
        }
    }

    /**
     * Generador de booleanos con distinta probabilidad para cada resultado.
     */
    public static class RandomBoolean {

        private float probability;

        /**
         * Crea un nuevo generador.
         *
         * @param probability la probabilidad de que genere <code>true</code>
         */
        public RandomBoolean(float probability) {
            this.probability = probability;
        }

        /**
         * Crea un nuevo generador como copia independiente de otro.
         *
         * @param copy el generador al que copiar
         */
        public RandomBoolean(RandomBoolean copy) {
            this.probability = copy.getProbability();
        }

        /**
         * Obtiene una copia independiente de este generador.
         *
         * @return una copia
         */
        public RandomBoolean copy() {
            return new RandomBoolean(this);
        }

        /**
         * Obtiene un nuevo <code>boolean</code> generado.
         *
         * @return el <code>boolean</code> aleatorio generado
         */
        public boolean generate() {
            return roll(probability);
        }

        /**
         * Obtiene la probabilidad de que genere <code>true</code>.
         *
         * @return la probabilidad de que genere <code>true</code>
         */
        public float getProbability() {
            return probability;
        }

        /**
         * Establece la probabilidad de que genere <code>true</code>.
         *
         * @param probability la probabilidad de que genere <code>true</code>
         */
        public void setProbability(float probability) {
            this.probability = probability;
        }

    }
}
