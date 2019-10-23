package org.panchosoft.pacman;

import java.awt.*;
import javax.swing.*;
import java.net.URL;
import org.panchosoft.pacman.util.pathfinding.AStarPathFinder;
import org.panchosoft.pacman.util.pathfinding.Path;

public class Pacman extends JFrame {

    public Pacman() {
        Juego pac = new Juego();

        this.getContentPane().add(pac);
        this.setSize(380, 550);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Pacman - Panchosoft Games");
        this.setLocationRelativeTo(null);
        try{
            this.setIconImage(new ImageIcon(this.getClass().getResource("/org/panchosoft/pacman/images/icono.png")).getImage());
        }catch(Exception ex){}
        this.setVisible(true);
        pac.inicializar();
        pac.start();

        this.pack();
        
    }

    public static void main(String[] args) {
        new Pacman();
    }
}

class Juego extends Canvas implements Runnable {

    /** The map on which the units will move */
    private PacmanMap map = new PacmanMap();
    /** The path finder we'll use to search our map */
    private AStarPathFinder finder;
    /** The last path found for the current unit */
    private Path path;
    /** The x coordinate of selected unit or -1 if none is selected */
    private int selectedx = -1;
    /** The y coordinate of selected unit or -1 if none is selected */
    private int selectedy = -1;
    /** The x coordinate of the target of the last path we searched for - used to cache and prevent constantly re-searching */
    private int lastFindX = -1;
    /** The y coordinate of the target of the last path we searched for - used to cache and prevent constantly re-searching */
    private int lastFindY = -1;
    private String objetivo;
    private PacMover mover = new PacMover(5);

    private int longitud;
    private boolean mostrarCostos;
    private boolean poderes;
    Dimension d;
    Font largefont = new Font("Tahoma", Font.BOLD, 24);
    Font smallfont = new Font("Tahoma", Font.BOLD, 14);
    Font miniaturafont = new Font("Tahoma",Font.ITALIC,7);
    FontMetrics fmsmall, fmlarge;
    Graphics graficos;
    Image imagen;
    Thread hilo;
    MediaTracker thetracker = null;
    Color colorPunto = new Color(192, 192, 0);
    int bigcolorPunto = 192;
    int dbigcolorPunto = -2;
    Color colorLaberinto;
    boolean enJuego = false;
    boolean mostrarTitulo = true;
    boolean asustado = false;
    boolean muriendo = false;
    final int retrasoPantalla = 120;
    final int tamanoBloque = 24;
    final int numBloques = 15;
    final int tamanoPantalla = numBloques * tamanoBloque;
    final int retrasoAnimacion = 8;
    final int pacmanRetraso = 2;
    final int ghostanimcount = 2;
    final int pacmananimcount = 4;
    final int maximoDeFantasmas = 12;
    final int velocidadPacman = 6;
    int animcount = retrasoAnimacion;
    int pacanimcount = pacmanRetraso;
    int pacanimdir = 1;
    int count = retrasoPantalla;
    int ghostanimpos = 0;
    int pacmananimpos = 0;
    int numeroFantasmas = 6;
    int vidasPacman, puntuacion;
    int contadorMuertes;
    int[] direccionenX, direccionenY;
    int[] fantasmaX, fantasmaY, fantasmaDireccionX, fantasmaDireccionY, velocidadesFantasmas;
    Image ghost1, ghost2, ghostasustado1, ghostasustado2;
    Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
    Image pacman3up, pacman3down, pacman3left, pacman3right;
    Image pacman4up, pacman4down, pacman4left, pacman4right;
    Image pacobjetivo;
    int pacmanx, pacmany, pacmandireccionenX, pacmandireccionenY;
    int reqdireccionenX, reqdireccionenY, viewdireccionenX, viewdireccionenY;
    int asustadocount, asustadotime;
    final int maxasustadotime = 120;
    final int minasustadotime = 20;
    final short datosNivel1[] = {
        19, 26, 26, 22, 9, 12, 19, 26, 22, 9, 12, 19, 26, 26, 22,
        37, 11, 14, 17, 26, 26, 20, 15, 17, 26, 26, 20, 11, 14, 37,
        17, 26, 26, 20, 11, 6, 17, 26, 20, 3, 14, 17, 26, 26, 20,
        21, 3, 6, 25, 22, 5, 21, 7, 21, 5, 19, 28, 3, 6, 21,
        21, 9, 8, 14, 21, 13, 21, 5, 21, 13, 21, 11, 8, 12, 21,
        25, 18, 26, 18, 24, 18, 28, 5, 25, 18, 24, 18, 26, 18, 28,
        6, 21, 7, 21, 7, 21, 11, 8, 14, 21, 7, 21, 7, 21, 03,
        4, 21, 5, 21, 5, 21, 11, 10, 14, 21, 5, 21, 5, 21, 1,
        12, 21, 13, 21, 13, 21, 11, 10, 14, 21, 13, 21, 13, 21, 9,
        19, 24, 26, 24, 26, 16, 26, 18, 26, 16, 26, 24, 26, 24, 22,
        21, 3, 2, 2, 6, 21, 15, 21, 15, 21, 3, 2, 2, 06, 21,
        21, 9, 8, 8, 4, 17, 26, 8, 26, 20, 1, 8, 8, 12, 21,
        17, 26, 26, 22, 13, 21, 11, 2, 14, 21, 13, 19, 26, 26, 20,
        37, 11, 14, 17, 26, 24, 22, 13, 19, 24, 26, 20, 11, 14, 37,
        25, 26, 26, 28, 3, 6, 25, 26, 28, 3, 6, 25, 26, 26, 28};
    final int validspeeds[] = {1, 2, 3, 4, 6, 8};
    final int maxspeed = 6;
    int velocidadActual = 3;
    short[] datosPantalla;

    public Juego() {
        finder = new AStarPathFinder(map, 500, false);

    }

    public void inicializar() {
        short i;

        getImages();

        datosPantalla = new short[numBloques * numBloques];

        Graphics g;
        d = size();
        this.getSize();
        setBackground(Color.black);
        g = this.getGraphics();

        g.setFont(smallfont);
        fmsmall = g.getFontMetrics();
        g.setFont(largefont);
        fmlarge = g.getFontMetrics();
        fantasmaX = new int[maximoDeFantasmas];
        fantasmaDireccionX = new int[maximoDeFantasmas];
        fantasmaY = new int[maximoDeFantasmas];
        fantasmaDireccionY = new int[maximoDeFantasmas];
        velocidadesFantasmas = new int[maximoDeFantasmas];
        direccionenX = new int[4];
        direccionenY = new int[4];
        iniciarJuego();
    }

    public void iniciarJuego() {
        vidasPacman = 3;
        puntuacion = 0;
        asustadotime = maxasustadotime;
        iniciarNivel();
        numeroFantasmas = 1;
        velocidadActual = 3;
        asustadotime = maxasustadotime;
    }

    public void iniciarNivel() {
        int i;
        for (i = 0; i < numBloques * numBloques; i++) {
            datosPantalla[i] = datosNivel1[i];
        }

        continuarNivel();
    }

    public void continuarNivel() {
        short i;
        int direccionenX = 1;
        int random;

        for (i = 0; i < numeroFantasmas; i++) {
            fantasmaY[i] = 7 * tamanoBloque;
            fantasmaX[i] = 7 * tamanoBloque;
            fantasmaDireccionY[i] = 0;
            fantasmaDireccionX[i] = direccionenX;
            direccionenX = -direccionenX;
            random = (int) (Math.random() * (velocidadActual + 1));
            if (random > velocidadActual) {
                random = velocidadActual;
            }
            velocidadesFantasmas[i]=validspeeds[random];

        }
        datosPantalla[7 * numBloques + 6] = 10;
        datosPantalla[7 * numBloques + 8] = 10;
        pacmanx = 7 * tamanoBloque;
        pacmany = 11 * tamanoBloque;
        pacmandireccionenX = 0;
        pacmandireccionenY = 0;
        reqdireccionenX = 0;
        reqdireccionenY = 0;
        viewdireccionenX = -1;
        viewdireccionenY = 0;
        muriendo = false;
        asustado = false;
    }

    public Image obtenImagen(String dir) {
        try {

            URL ruta = getClass().getResource(dir);

            return this.getToolkit().getImage(ruta);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public void getImages() {
        thetracker = new MediaTracker(this);

        ghost1 = obtenImagen("/org/panchosoft/pacman/images/Ghost1.gif");
        thetracker.addImage(ghost1, 0);
        ghost2 = obtenImagen("/org/panchosoft/pacman/images/Ghost2.gif");
        thetracker.addImage(ghost2, 0);
        ghostasustado1 = obtenImagen("/org/panchosoft/pacman/images/GhostScared1.gif");
        thetracker.addImage(ghostasustado1, 0);
        ghostasustado2 = obtenImagen("/org/panchosoft/pacman/images/GhostScared2.gif");
        thetracker.addImage(ghostasustado2, 0);

        pacman1 = obtenImagen("/org/panchosoft/pacman/images/PacMan1.gif");
        thetracker.addImage(pacman1, 0);
        pacman2up = obtenImagen("/org/panchosoft/pacman/images/PacMan2up.gif");
        thetracker.addImage(pacman2up, 0);
        pacman3up = obtenImagen("/org/panchosoft/pacman/images/PacMan3up.gif");
        thetracker.addImage(pacman3up, 0);
        pacman4up = obtenImagen("/org/panchosoft/pacman/images/PacMan4up.gif");
        thetracker.addImage(pacman4up, 0);

        pacman2down = obtenImagen("/org/panchosoft/pacman/images/PacMan2down.gif");
        thetracker.addImage(pacman2down, 0);
        pacman3down = obtenImagen("/org/panchosoft/pacman/images/PacMan3down.gif");
        thetracker.addImage(pacman3down, 0);
        pacman4down = obtenImagen("/org/panchosoft/pacman/images/PacMan4down.gif");
        thetracker.addImage(pacman4down, 0);

        pacman2left = obtenImagen("/org/panchosoft/pacman/images/PacMan2left.gif");
        thetracker.addImage(pacman2left, 0);
        pacman3left = obtenImagen("/org/panchosoft/pacman/images/PacMan3left.gif");
        thetracker.addImage(pacman3left, 0);
        pacman4left = obtenImagen("/org/panchosoft/pacman/images/PacMan4left.gif");
        thetracker.addImage(pacman4left, 0);

        pacman2right = obtenImagen("/org/panchosoft/pacman/images/PacMan2right.gif");
        thetracker.addImage(pacman2right, 0);
        pacman3right = obtenImagen("/org/panchosoft/pacman/images/PacMan3right.gif");
        thetracker.addImage(pacman3right, 0);
        pacman4right = obtenImagen("/org/panchosoft/pacman/images/PacMan4right.gif");
        thetracker.addImage(pacman4right, 0);

        pacobjetivo = obtenImagen("/org/panchosoft/pacman/images/02.png");
        try {
            thetracker.waitForAll();
        } catch (InterruptedException e) {
            return;
        }
    }

    public boolean keyDown(Event e, int key) {
        if (enJuego) {
            if (key == Event.LEFT) {
                reqdireccionenX = -1;
                reqdireccionenY = 0;
            } else if (key == Event.RIGHT) {
                reqdireccionenX = 1;
                reqdireccionenY = 0;
            } else if (key == Event.UP) {
                reqdireccionenX = 0;
                reqdireccionenY = -1;
            } else if (key == Event.DOWN) {
                reqdireccionenX = 0;
                reqdireccionenY = 1;
            } else if (key == Event.ESCAPE) {
                enJuego = false;
            } else if(key == 'c' || key == 'C'){
                if(mostrarCostos) mostrarCostos = false;
                else mostrarCostos = true;
            } else if(key == 'm' || key == 'M'){
                poderes = true;
            }

        } else {
            if (key == 's' || key == 'S') {
                enJuego = true;
                iniciarJuego();
            }


        }
        return true;
    }

    public boolean keyUp(Event e, int key) {
        if (key == Event.LEFT || key == Event.RIGHT || key == Event.UP || key == Event.DOWN) {
            reqdireccionenX = 0;
            reqdireccionenY = 0;
        }
        return true;
    }

    public void paint(Graphics g) {
        String s;
        Graphics gg;

        if (graficos == null && d != null && d.width > 0 && d.height > 0) {
            imagen = createImage(d.width, d.height);
            graficos = imagen.getGraphics();
        }
        if (graficos == null || imagen == null) {
            return;
        }

        graficos.setColor(Color.black);
        graficos.fillRect(0, 0, d.width, d.height);

        DrawMaze();
        Drawpuntuacion();
        DoAnim();
        if (enJuego) {
            PlayGame();
        } else {
            PlayDemo();
        }

        g.drawImage(imagen, 0, 0, this);
    }

    public void DoAnim() {
        animcount--;
        if (animcount <= 0) {
            animcount = retrasoAnimacion;
            ghostanimpos++;
            if (ghostanimpos >= ghostanimcount) {
                ghostanimpos = 0;
            }
        }
        pacanimcount--;
        if (pacanimcount <= 0) {
            pacanimcount = pacmanRetraso;
            pacmananimpos = pacmananimpos + pacanimdir;
            if (pacmananimpos == (pacmananimcount - 1) || pacmananimpos == 0) {
                pacanimdir = -pacanimdir;
            }
        }
    }

    public void PlayGame() {
        if (muriendo) {
            muerto();
        } else {
            verificarAsustado();
            moverPacman();
            dibujarPacman();
            moverFantasmas();
            verificarLaberinto();
        }
    }

    public void PlayDemo() {
        verificarAsustado();
        moverFantasmas();
        ShowIntroScreen();
    }

    public void muerto() {
        int k;

        contadorMuertes--;
        k = (contadorMuertes & 15) / 4;
        switch (k) {
            case 0:
                graficos.drawImage(pacman4up, pacmanx + 1, pacmany + 1, this);
                break;
            case 1:
                graficos.drawImage(pacman4right, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                graficos.drawImage(pacman4down, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                graficos.drawImage(pacman4left, pacmanx + 1, pacmany + 1, this);
        }
        if (contadorMuertes == 0) {
            vidasPacman--;
            if (vidasPacman == 0) {
                enJuego = false;
            }
            continuarNivel();
        }
    }

    public void moverFantasmas() {
        short i;
        int pos;
        int count;
        boolean libreIzquierda = false, libreDerecha = false, libreAbajo = false, libreArriba = false;
        if (fantasmaX == null && fantasmaY == null) {
            return;
        }
        for (i = 0; i < numeroFantasmas; i++) {
            if (fantasmaX[i] % tamanoBloque == 0 && fantasmaY[i] % tamanoBloque == 0) {
                pos = fantasmaX[i] / tamanoBloque + numBloques * (int) (fantasmaY[i] / tamanoBloque);

                count = 0;

                // Verificar si está libre la pocisión izquierda
                if ((datosPantalla[pos] & 1) == 0 && fantasmaDireccionX[i] != 1) {

                    direccionenX[count] = -1;
                    direccionenY[count] = 0;
                    libreIzquierda = true;
                    count++;

                }
                // Verificar si está libre la pocisión de arriba
                if ((datosPantalla[pos] & 2) == 0 && fantasmaDireccionY[i] != 1) {
                    direccionenX[count] = 0;
                    direccionenY[count] = -1;
                    libreArriba = true;
                    count++;
                }
                // compara si esta libre la posicion de la derecha
                if ((datosPantalla[pos] & 4) == 0 && fantasmaDireccionX[i] != -1) {
                    direccionenX[count] = 1;
                    direccionenY[count] = 0;
                    libreDerecha = true;
                    count++;
                }
                // compara si esta libre la posicion de abajo
                if ((datosPantalla[pos] & 8) == 0 && fantasmaDireccionY[i] != -1) {
                    direccionenX[count] = 0;
                    direccionenY[count] = 1;
                    libreAbajo = true;
                    count++;
                }
                if (count == 0) {
                    if ((datosPantalla[pos] & 15) == 15) {
                        fantasmaDireccionX[i] = 0;
                        fantasmaDireccionY[i] = 0;
                    } else {
                        fantasmaDireccionX[i] = -fantasmaDireccionX[i];
                        fantasmaDireccionY[i] = -fantasmaDireccionY[i];
                    }
                } else {
                    map.clearVisited();

                    int fx = valorX((double) fantasmaX[i] / numBloques);
                    int fy = valorY((double) fantasmaY[i] / numBloques);
                    int px = valorX((double)pacmanx / numBloques);
                    int py = valorY((double)pacmany / numBloques);


                    try{
                        path = finder.findPath(mover, fx, fy, px, py);
                        if(path!=null){
                            this.longitud = path.getLength();
                            int finalx = path.getStep(path.getLength()-1).getX();
                            int finaly = path.getStep(path.getLength()-1).getY();

                            this.objetivo = "["+finalx + "," + finaly+"]";

                        }
                    }catch(Exception ex){
                        System.out.println("Error: ");
                        System.out.println("Fantasma: " + fx + ","+fy);
                        System.out.println("Pacman: " + px + ","+ py);
                        System.out.println(ex.getMessage());
                    }


                    // Moviendo respecto al algoritmo
                    if(path != null && !asustado && enJuego){
                            int sx = path.getStep(1).getX();
                            int sy = path.getStep(1).getY();

                            int direx = 0, direy = 0;
                            if(sx == fx) direx = 0;
                            if(sx > fx) direx = 1;
                            if(sx < fx) direx = -1;
                            if(sy == fy) direy = 0;
                            if(sy > fy) direy = 1;
                            if(sy < fy) direy = -1;


                            fantasmaDireccionY[i] = direy;
                            fantasmaDireccionX[i] = direx;
                        }

                    // Si el fantasma está asustado, lo movemos al azar
                    if(asustado || !enJuego){
                        //Movimiento al azar
                      count=(int)(Math.random()*count);
                      if (count>3) count=3;
                      fantasmaDireccionX[i]=direccionenX[count];
                      fantasmaDireccionY[i]=direccionenY[count];
                    }

                }
            }
            fantasmaX[i] = fantasmaX[i] + (fantasmaDireccionX[i] * velocidadesFantasmas[i]);
            fantasmaY[i] = fantasmaY[i] + (fantasmaDireccionY[i] * velocidadesFantasmas[i]);
            dibujarFantasma(fantasmaX[i] + 1, fantasmaY[i] + 1);

            if (pacmanx > (fantasmaX[i] - 12) && pacmanx < (fantasmaX[i] + 12) &&
                    pacmany > (fantasmaY[i] - 12) && pacmany < (fantasmaY[i] + 12) && enJuego) {
                if (asustado) {
                    puntuacion += 10;
                    fantasmaX[i] = 7 * tamanoBloque;
                    fantasmaY[i] = 7 * tamanoBloque;
                } else {
                    muriendo = true;
                    contadorMuertes = 64;
                }
            }
        }
    }

    public int valorX(double x) {
        if (x == 0.0) {
            return 0;
        } else if (x <= 1.6) {
            return 1;
        } else if (x <= 3.2) {
            return 2;
        } else if (x <= 4.8) {
            return 3;
        } else if (x <= 6.4) {
            return 4;
        } else if (x <= 8.0) {
            return 5;
        } else if (x <= 9.6) {
            return 6;
        } else if (x <= 11.2 || x <= 11.6) {
            return 7;
        } else if (x <= 12.8) {
            return 8;
        } else if (x <= 14.4) {
            return 9;
        } else if (x <= 16.0) {
            return 10;
        } else if (x <= 17.6) {
            return 11;
        } else if (x <= 19.2) {
            return 12;
        } else if (x <= 20.8) {
            return 13;
        } else if (x <= 22.4) {
            return 14;
        }

        return -1;
    }
    public int valorY(double x){
        if (x == 0.0) {
            return 0;
        } else if (x <= 1.6) {
            return 1;
        } else if (x <= 3.2) {
            return 2;
        } else if (x <= 4.8) {
            return 3;
        } else if (x <= 6.4) {
            return 4;
        } else if (x <= 8.0) {
            return 5;
        } else if (x <= 9.6 || x <= 9.2) {
            return 6;
        } else if (x <= 11.2) {
            return 7;
        } else if (x <= 12.8) {
            return 8;
        } else if (x <= 14.4) {
            return 9;
        } else if (x <= 16.0) {
            return 10;
        } else if (x <= 17.6) {
            return 11;
        } else if (x <= 19.2) {
            return 12;
        } else if (x <= 20.8) {
            return 13;
        } else if (x <= 22.4) {
            return 14;
        }

        return -1;
    }

    public void dibujarFantasma(int x, int y) {
        if (ghostanimpos == 0 && !asustado) {
            graficos.drawImage(ghost1, x, y, this);
        } else if (ghostanimpos == 1 && !asustado) {
            graficos.drawImage(ghost2, x, y, this);
        } else if (ghostanimpos == 0 && asustado) {
            graficos.drawImage(ghostasustado1, x, y, this);
        } else if (ghostanimpos == 1 && asustado) {
            graficos.drawImage(ghostasustado2, x, y, this);
        }
    }

    public void moverPacman() {
        int pos;
        short ch;

        if (reqdireccionenX == -pacmandireccionenX && reqdireccionenY == -pacmandireccionenY) {
            pacmandireccionenX = reqdireccionenX;
            pacmandireccionenY = reqdireccionenY;
            viewdireccionenX = pacmandireccionenX;
            viewdireccionenY = pacmandireccionenY;
        }
        if (pacmanx % tamanoBloque == 0 && pacmany % tamanoBloque == 0) {
            pos = pacmanx / tamanoBloque + numBloques * (int) (pacmany / tamanoBloque);

            ch = datosPantalla[pos];
            if ((ch & 16) != 0) {
                datosPantalla[pos] = (short) (ch & 15);
                puntuacion++;
            }
            if ((ch & 32) != 0) {
                asustado = true;
                asustadocount = asustadotime;
                datosPantalla[pos] = (short) (ch & 15);
                puntuacion += 5;
            }

            if (reqdireccionenX != 0 || reqdireccionenY != 0) {
                if (!((reqdireccionenX == -1 && reqdireccionenY == 0 && (ch & 1) != 0) ||
                        (reqdireccionenX == 1 && reqdireccionenY == 0 && (ch & 4) != 0) ||
                        (reqdireccionenX == 0 && reqdireccionenY == -1 && (ch & 2) != 0) ||
                        (reqdireccionenX == 0 && reqdireccionenY == 1 && (ch & 8) != 0))) {
                    pacmandireccionenX = reqdireccionenX;
                    pacmandireccionenY = reqdireccionenY;
                    viewdireccionenX = pacmandireccionenX;
                    viewdireccionenY = pacmandireccionenY;
                }
            }

            // Check for standstill
            if ((pacmandireccionenX == -1 && pacmandireccionenY == 0 && (ch & 1) != 0) ||
                    (pacmandireccionenX == 1 && pacmandireccionenY == 0 && (ch & 4) != 0) ||
                    (pacmandireccionenX == 0 && pacmandireccionenY == -1 && (ch & 2) != 0) ||
                    (pacmandireccionenX == 0 && pacmandireccionenY == 1 && (ch & 8) != 0)) {
                pacmandireccionenX = 0;
                pacmandireccionenY = 0;
            }
        }
        pacmanx = pacmanx + velocidadPacman * pacmandireccionenX;
        pacmany = pacmany + velocidadPacman * pacmandireccionenY;
    }

    public void dibujarPacman() {
        if (viewdireccionenX == -1) {
            dibujarPacmanLeft();
        } else if (viewdireccionenX == 1) {
            dibujarPacmanRight();
        } else if (viewdireccionenY == -1) {
            dibujarPacmanUp();
        } else {
            dibujarPacmanDown();
        }
    }

    public void dibujarPacmanUp() {
        switch (pacmananimpos) {
            case 1:
                graficos.drawImage(pacman2up, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                graficos.drawImage(pacman3up, pacmanx + 1, pacmany + 1, this);
                break;
            case 3:
                graficos.drawImage(pacman4up, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                graficos.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
                break;
        }
    }

    public void dibujarPacmanDown() {
        switch (pacmananimpos) {
            case 1:
                graficos.drawImage(pacman2down, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                graficos.drawImage(pacman3down, pacmanx + 1, pacmany + 1, this);
                break;
            case 3:
                graficos.drawImage(pacman4down, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                graficos.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
                break;
        }
    }

    public void dibujarPacmanLeft() {
        switch (pacmananimpos) {
            case 1:
                graficos.drawImage(pacman2left, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                graficos.drawImage(pacman3left, pacmanx + 1, pacmany + 1, this);
                break;
            case 3:
                graficos.drawImage(pacman4left, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                graficos.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
                break;
        }
    }

    public void dibujarPacmanRight() {
        switch (pacmananimpos) {
            case 1:
                graficos.drawImage(pacman2right, pacmanx + 1, pacmany + 1, this);
                break;
            case 2:
                graficos.drawImage(pacman3right, pacmanx + 1, pacmany + 1, this);
                break;
            case 3:
                graficos.drawImage(pacman4right, pacmanx + 1, pacmany + 1, this);
                break;
            default:
                graficos.drawImage(pacman1, pacmanx + 1, pacmany + 1, this);
                break;
        }
    }

    public void DrawMaze() {
        short i = 0;
        int x, y;

        bigcolorPunto = bigcolorPunto + dbigcolorPunto;
        if (bigcolorPunto <= 64 || bigcolorPunto >= 192) {
            dbigcolorPunto = -dbigcolorPunto;
        }

        for (y = 0; y < tamanoPantalla; y += tamanoBloque) {
            for (x = 0; x < tamanoPantalla; x += tamanoBloque) {
                graficos.setColor(colorLaberinto);
                if ((datosPantalla[i] & 1) != 0) {
                    graficos.drawLine(x, y, x, y + tamanoBloque - 1);
                }
                if ((datosPantalla[i] & 2) != 0) {
                    graficos.drawLine(x, y, x + tamanoBloque - 1, y);
                }
                if ((datosPantalla[i] & 4) != 0) {
                    graficos.drawLine(x + tamanoBloque - 1, y, x + tamanoBloque - 1, y + tamanoBloque - 1);
                }
                if ((datosPantalla[i] & 8) != 0) {
                    graficos.drawLine(x, y + tamanoBloque - 1, x + tamanoBloque - 1, y + tamanoBloque - 1);
                }
                if ((datosPantalla[i] & 16) != 0) {
                    graficos.setColor(colorPunto);
                    graficos.fillRect(x + 11, y + 11, 2, 2);
                }
                if ((datosPantalla[i] & 32) != 0) {
                    graficos.setColor(new Color(224, 224 - bigcolorPunto, bigcolorPunto));
                    graficos.fillRect(x + 8, y + 8, 8, 8);
                }

                i++;
            }
        }
                     if(mostrarCostos){
                        if(path !=null){
                            Float costo = 0.0f;
                            for(int o = 1; o < path.getLength()-1; o++){
                                int afnx = path.getStep(o-1).getX();
                                int afny = path.getStep(o-1).getY();
                                int fnx = path.getStep(o).getX();
                                int fny = path.getStep(o).getY();

                                costo += finder.getMovementCost(this.mover, afnx, afny, fnx, fny);

                                graficos.setFont(this.miniaturafont);
                                //System.out.println("fx: " + (fx*24) + ", fy: " + (fy*24));
                                graficos.drawString(""+costo, (fnx*24)+5, (fny*24)+7);
                                //graficos.drawImage(pacman4right, (fnx*24)+5, (fny*24)+5 + 1, this);

                            }

                        }else{

                        }
                    }
    }

    public void ShowIntroScreen() {
        String s;

        graficos.setFont(largefont);

        graficos.setColor(new Color(0, 32, 48));
        graficos.fillRect(16, tamanoPantalla / 2 - 40, tamanoPantalla - 32, 80);
        graficos.setColor(Color.white);
        graficos.drawRect(16, tamanoPantalla / 2 - 40, tamanoPantalla - 32, 80);

        if (mostrarTitulo) {
            try {
                s = "A* Pacman";
                asustado = false;

                graficos.setColor(Color.white);
                graficos.drawString(s, (tamanoPantalla - fmlarge.stringWidth(s)) / 2 + 2, tamanoPantalla / 2 - 20 + 2);
                graficos.setColor(new Color(96, 128, 255));
                graficos.drawString(s, (tamanoPantalla - fmlarge.stringWidth(s)) / 2, tamanoPantalla / 2 - 20);

                s = "(c)2008 Panchosoft Labs";
                graficos.setFont(smallfont);
                graficos.setColor(new Color(255, 160, 64));
                graficos.drawString(s, (tamanoPantalla - fmsmall.stringWidth(s)) / 2, tamanoPantalla / 2 + 10);

                s = "staff@panchosoft.com";
                graficos.setColor(new Color(255, 160, 64));
                graficos.drawString(s, (tamanoPantalla - fmsmall.stringWidth(s)) / 2, tamanoPantalla / 2 + 30);
            } catch (Exception ex) {
            }
        } else {
            graficos.setFont(smallfont);
            graficos.setColor(new Color(96, 128, 255));
            s = "Presiona 'S' para jugar";
            graficos.drawString(s, (tamanoPantalla - fmsmall.stringWidth(s)) / 2, tamanoPantalla / 2 - 10);
            graficos.setColor(new Color(255, 160, 64));
            s = "Usa las flechas del teclado para moverte";
            graficos.drawString(s, (tamanoPantalla - fmsmall.stringWidth(s)) / 2, tamanoPantalla / 2 + 20);
            asustado = true;
        }
        count--;
        if (count <= 0) {
            count = retrasoPantalla;
            mostrarTitulo = !mostrarTitulo;
        }
    }

    public void Drawpuntuacion() {
        int i;
        String s;

        graficos.setFont(smallfont);
        graficos.setColor(new Color(96, 128, 255));
        s = "Puntos: " + puntuacion;
        graficos.drawString(s, tamanoPantalla / 2 + 96, tamanoPantalla + 16);
        for (i = 0; i < vidasPacman; i++) {
            graficos.drawImage(pacman3left, i * 28 + 8, tamanoPantalla + 1, this);
        }
        graficos.drawString("Algoritmo A*:", 10, tamanoPantalla + 46);
        graficos.drawString("Distancia: " + (longitud-1), 30, tamanoPantalla + 66);
        
        graficos.drawString("Objetivo: " + (objetivo), 160, tamanoPantalla + 66);
        graficos.drawString("Ver costo(c): ", 30, tamanoPantalla + 96);
        if(mostrarCostos)
            graficos.drawImage(this.pacobjetivo, 125, tamanoPantalla + 82, this);

    }

    public void verificarAsustado() {
        asustadocount--;
        if (asustadocount <= 0) {
            asustado = false;
        }

        if (asustado && asustadocount >= 30) {
            colorLaberinto = new Color(192, 32, 255);
        } else {
            colorLaberinto = new Color(32, 192, 255);
        }

        if (asustado) {
            datosPantalla[7 * numBloques + 6] = 11;
            datosPantalla[7 * numBloques + 8] = 14;
        } else {
            datosPantalla[7 * numBloques + 6] = 10;
            datosPantalla[7 * numBloques + 8] = 10;
        }
    }

    public void verificarLaberinto() {
        short i = 0;
        boolean finished = true;

        while (i < numBloques * numBloques && finished) {
            if ((datosPantalla[i] & 48) != 0) {
                finished = false;
            }
            i++;
        }
        if (finished) {
            puntuacion += 50;
            Drawpuntuacion();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            if (numeroFantasmas < maximoDeFantasmas) {
                numeroFantasmas++;
            }
            if (velocidadActual < maxspeed) {
                velocidadActual++;
            }
            asustadotime = asustadotime - 20;
            if (asustadotime < minasustadotime) {
                asustadotime = minasustadotime;
            }
            iniciarNivel();
        }
    }

    public void run() {
        long starttime;
        Graphics g;

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        g = getGraphics();

        while (true) {
            starttime = System.currentTimeMillis();
            try {
                paint(g);
                starttime += 40;

                Thread.sleep(Math.max(0, starttime - System.currentTimeMillis()));

            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                break;
            }

        }
    }

    public void start() {
        if (hilo == null) {
            hilo = new Thread(this);
            hilo.start();
        }
    }

    public void stop() {
        if (hilo != null) {
            hilo.stop();
            hilo = null;
        }
    }
}