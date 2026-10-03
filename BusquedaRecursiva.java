/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package back;

import java.util.Random;

/**
 *
 * @author Santiago
 */
public class BusquedaRecursiva {

    private int[] vector;
    private int longitud;

    public int[] retornarVector() {
        Random rd = new Random();
        for (int i = 0; i < vector.length; i++) {
            vector[i] = rd.nextInt(13);
        }
        return vector;
    }

    public void imprimirVector(int[] vector) {
        System.out.println("Imprimiendo vector: ");
        for (int i = 0; i < vector.length; i++) {
            System.out.println("[" + i + "]->" + vector[i]);
        }
        System.out.println("**************************");
    }

    public BusquedaRecursiva(int longitud) {
        this.longitud = longitud;
        vector = new int[longitud];
    }

    public int[] getVector() {
        return vector;
    }

    public void setVector(int[] vector) {
        this.vector = vector;
    }

    public int getLongitud() {
        return longitud;
    }

    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }

    //------------------------------------------------------------------
    public static boolean secuencial(int[] vector, int indice, int valor) {
        if (indice == vector.length) {
            return false;
        }
        if (vector[indice] == valor) {
            return true;
        }
        return secuencial(vector, indice + 1, valor);
    }

    public int buscarIndice(int[] vector, int indice, int valor) {
        if (indice >= vector.length) {
            return -1;
        }
        if (vector[indice] == valor) {
            return indice;
        }
        return buscarIndice(vector, indice + 1, valor);
    }

    public int buscarBinaria(int[] vector, int inicio, int fin, int valor) {
        if (inicio > fin) {
            return -1;
        }
        int medio = (inicio + fin) / 2;
        if (vector[medio] == valor) {
            return medio;
        }
        if (valor < vector[medio]) {
            return buscarBinaria(vector, inicio, medio - 1, valor); // mitad izquierda
        }
        return buscarBinaria(vector, medio + 1, fin, valor);        // mitad derecha
    }

    public void quicksort(int[] vector, int inicio, int fin) {
        if (inicio < fin) {
            int posicionPivote = particionar(vector, inicio, fin);
            quicksort(vector, inicio, posicionPivote - 1);
            quicksort(vector, posicionPivote + 1, fin);
        }
    }

    private int particionar(int[] vector, int inicio, int fin) {
        int pivote = vector[fin];
        int i = inicio - 1;
        int aux;
        for (int j = inicio; j < fin; j++) {
            if (vector[j] <= pivote) {
                i++;
                aux = vector[i];
                vector[i] = vector[j];
                vector[j] = aux;
            }
        }
        aux = vector[i + 1];
        vector[i + 1] = vector[fin];
        vector[fin] = aux;
        return i + 1;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BusquedaRecursiva objeto = new BusquedaRecursiva(10);
        int[] vectorOriginal = objeto.retornarVector();
        objeto.imprimirVector(vectorOriginal);
        int valor = 12;
        System.out.println("Valor buscado.." + valor);
        if (secuencial(vectorOriginal, 0, valor)) {
            System.out.println("Elemento encontrado");
        } else {
            System.out.println("Elemento no encontrado");
        }

        int posicion = objeto.buscarIndice(vectorOriginal, 0, valor);
        if (posicion != -1) {
            System.out.println("Elemento encontrado en la posicion: " + posicion);
        } else {
            System.out.println("Elemento no encontrado");
        }

        objeto.quicksort(vectorOriginal, 0, vectorOriginal.length - 1);
        objeto.imprimirVector(vectorOriginal);

        int posicion2 = objeto.buscarBinaria(vectorOriginal, 0, vectorOriginal.length - 1, valor);
        if (posicion2 != -1) {
            System.out.println("Elemento encontrado en la posicion: " + posicion2);
        } else {
            System.out.println("Elemento no encontrado");
        }
        // TODO code application logic here
    }

}
