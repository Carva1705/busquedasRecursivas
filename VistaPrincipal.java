package vista;

import back.BusquedaRecursiva;
import java.awt.*;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Ventana principal con menú. Cada opción abre una ventana hija con una
 * búsqueda específica del back. Solo puede haber una ventana hija activa.
 */
public class VistaPrincipal extends JFrame {

    // Paleta: blanco, minimalista, un único color de acento
    private static final Color BG = Color.WHITE;
    private static final Color BORDER = new Color(0xE4, 0xE4, 0xE4);
    private static final Color TEXT = new Color(0x1A, 0x1A, 0x1A);
    private static final Color MUTED = new Color(0x8A, 0x8A, 0x8A);
    private static final Color ACCENT = new Color(0x2F, 0x6F, 0x4F);
    private static final Color BOX_BORDER = new Color(0xDD, 0xDD, 0xDD);

    private final BusquedaRecursiva back = new BusquedaRecursiva(10);
    private final int[] vector = back.retornarVector();
    private JFrame ventanaActiva; // única ventana hija permitida

    public VistaPrincipal() {
        super("Búsquedas recursivas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(BG);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(28, 32, 28, 32));
        setLayout(new BorderLayout());

        JMenu menu = new JMenu("Búsquedas");
        agregarOpcion(menu, "Secuencial",
                "Recorre el vector posición por posición hasta encontrar el valor.",
                this::buscarSecuencial);
        agregarOpcion(menu, "Por índice",
                "Igual que la secuencial, pero devuelve la posición del valor.",
                this::buscarIndice);
        agregarOpcion(menu, "Binaria",
                "Requiere un vector ordenado: se ordena una copia con quicksort.",
                this::buscarBinaria);

        JMenuBar barra = new JMenuBar();
        barra.add(menu);
        setJMenuBar(barra);

        JPanel board = new JPanel();
        pintar(board, vector, -1);
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(BG);
        centro.setBorder(new EmptyBorder(20, 0, 0, 0));
        centro.add(board, BorderLayout.NORTH);

        add(encabezado("Búsquedas recursivas", "Elige una búsqueda en el menú «Búsquedas»."),
                BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        setSize(700, 320);
        setLocationRelativeTo(null);
    }

    private void agregarOpcion(JMenu menu, String titulo, String descripcion,
                               Function<Integer, Resultado> metodo) {
        JMenuItem item = new JMenuItem(titulo);
        item.addActionListener(e -> abrirVentana(titulo, descripcion, metodo));
        menu.add(item);
    }

    /** Cierra la hija anterior (si existe) y abre la nueva. */
    private void abrirVentana(String titulo, String descripcion, Function<Integer, Resultado> metodo) {
        if (ventanaActiva != null) {
            ventanaActiva.dispose();
        }
        ventanaActiva = new VentanaBusqueda(titulo, descripcion, metodo);
        ventanaActiva.setVisible(true);
    }

    // ---------- Llamadas al BACK ----------

    private Resultado buscarSecuencial(int valor) {
        boolean encontrado = BusquedaRecursiva.secuencial(vector, 0, valor);
        return new Resultado(vector, -1, encontrado);
    }

    private Resultado buscarIndice(int valor) {
        int pos = back.buscarIndice(vector, 0, valor);
        return new Resultado(vector, pos, pos != -1);
    }

    private Resultado buscarBinaria(int valor) {
        int[] ordenado = vector.clone();
        back.quicksort(ordenado, 0, ordenado.length - 1);
        int pos = back.buscarBinaria(ordenado, 0, ordenado.length - 1, valor);
        return new Resultado(ordenado, pos, pos != -1);
    }

    /** Lo que devuelve cada búsqueda a la vista. */
    private static class Resultado {
        final int[] vector;
        final int posicion;
        final boolean encontrado;

        Resultado(int[] vector, int posicion, boolean encontrado) {
            this.vector = vector;
            this.posicion = posicion;
            this.encontrado = encontrado;
        }
    }

    // ---------- Ventana hija genérica ----------

    private class VentanaBusqueda extends JFrame {

        VentanaBusqueda(String titulo, String descripcion, Function<Integer, Resultado> metodo) {
            super("Búsqueda " + titulo.toLowerCase());
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            getContentPane().setBackground(BG);
            ((JComponent) getContentPane()).setBorder(new EmptyBorder(24, 28, 24, 28));
            setLayout(new BorderLayout());

            // Controles
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
            spinner.setPreferredSize(new Dimension(100, 30));
            JButton buscar = new JButton("Buscar");
            buscar.setFocusPainted(false);
            buscar.setBackground(ACCENT);
            buscar.setForeground(Color.WHITE);
            buscar.setBorder(new LineBorder(ACCENT, 1));
            buscar.setPreferredSize(new Dimension(110, 32));

            JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            controles.setBackground(BG);
            controles.add(etiqueta("Valor a buscar"));
            controles.add(spinner);
            controles.add(buscar);

            // Tablero del vector
            JPanel board = new JPanel();
            pintar(board, vector, -1);
            JPanel centro = new JPanel(new BorderLayout());
            centro.setBackground(BG);
            centro.setBorder(new EmptyBorder(20, 0, 20, 0));
            centro.add(board, BorderLayout.NORTH);

            // Estadísticas
            JLabel lblResultado = new JLabel("—");
            JLabel lblPosicion = new JLabel("—");
            JLabel lblTiempo = new JLabel("—");
            JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            stats.setBackground(BG);
            stats.add(estadistica("Resultado", lblResultado, TEXT));
            stats.add(estadistica("Posición", lblPosicion, TEXT));
            stats.add(estadistica("Tiempo", lblTiempo, ACCENT));

            JPanel arriba = new JPanel(new BorderLayout(0, 16));
            arriba.setBackground(BG);
            arriba.add(encabezado("Búsqueda " + titulo.toLowerCase(), descripcion), BorderLayout.NORTH);
            arriba.add(controles, BorderLayout.CENTER);

            add(arriba, BorderLayout.NORTH);
            add(centro, BorderLayout.CENTER);
            add(stats, BorderLayout.SOUTH);

            buscar.addActionListener(e -> {
                long inicio = System.nanoTime();
                Resultado r = metodo.apply((Integer) spinner.getValue());
                long nanos = System.nanoTime() - inicio;

                pintar(board, r.vector, r.posicion);
                lblResultado.setText(r.encontrado ? "Encontrado" : "No encontrado");
                lblPosicion.setText(r.posicion >= 0 ? String.valueOf(r.posicion) : "—");
                lblTiempo.setText(String.format("%.3f ms", nanos / 1_000_000.0));
            });

            setSize(700, 380);
            setLocation(VistaPrincipal.this.getX() + 40, VistaPrincipal.this.getY() + 60);
        }
    }

    // ---------- Helpers de estilo ----------

    private static JPanel encabezado(String titulo, String subtitulo) {
        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.PLAIN, 21));
        t.setForeground(TEXT);
        JLabel s = new JLabel(subtitulo);
        s.setFont(new Font("SansSerif", Font.PLAIN, 13));
        s.setForeground(MUTED);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.add(t);
        p.add(Box.createVerticalStrut(4));
        p.add(s);
        return p;
    }

    /** Dibuja una casilla por valor; la posición resaltada va en color de acento. */
    private static void pintar(JPanel board, int[] v, int resaltado) {
        board.removeAll();
        board.setBackground(BG);
        board.setLayout(new GridLayout(1, Math.max(v.length, 1), 5, 5));
        for (int i = 0; i < v.length; i++) {
            JLabel caja = new JLabel(String.valueOf(v[i]), SwingConstants.CENTER);
            caja.setOpaque(true);
            caja.setFont(new Font("Monospaced", Font.PLAIN, 13));
            caja.setPreferredSize(new Dimension(38, 36));
            boolean activa = (i == resaltado);
            caja.setBackground(activa ? ACCENT : Color.WHITE);
            caja.setForeground(activa ? Color.WHITE : TEXT);
            caja.setBorder(new LineBorder(activa ? ACCENT : BOX_BORDER, 1));
            board.add(caja);
        }
        board.revalidate();
        board.repaint();
    }

    private static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(MUTED);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return l;
    }

    private static JPanel estadistica(String texto, JLabel valor, Color color) {
        JLabel et = etiqueta(texto);
        et.setAlignmentX(Component.LEFT_ALIGNMENT);
        valor.setForeground(color);
        valor.setFont(new Font("Monospaced", Font.PLAIN, 15));
        valor.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(0, 0, 0, 32));
        p.add(et);
        p.add(Box.createVerticalStrut(4));
        p.add(valor);
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VistaPrincipal().setVisible(true));
    }
}