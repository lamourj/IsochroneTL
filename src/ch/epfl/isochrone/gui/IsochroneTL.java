/**
 * Classe principale du programme. <p>
 * Se charge de construire l'interface graphique et d'afficher 
 * la fenêtre du programme à l'écran, avec laquelle l'utilisateur 
 * peut ensuite interagir.
 * 
 * 05.05.2014
 *
 * @author Julien Lamour (236517)
 * @author Josselin Held (239612)
 */
package ch.epfl.isochrone.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JViewport;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.epfl.isochrone.geo.PointOSM;
import ch.epfl.isochrone.geo.PointWGS84;
import ch.epfl.isochrone.tiledmap.CachedTileProvider;
import ch.epfl.isochrone.tiledmap.ColorTable;
import ch.epfl.isochrone.tiledmap.IsochroneTileProvider;
import ch.epfl.isochrone.tiledmap.OSMTileProvider;
import ch.epfl.isochrone.tiledmap.TileProvider;
import ch.epfl.isochrone.tiledmap.TrajetTileProvider;
import ch.epfl.isochrone.tiledmap.TransparentTileProvider;
import ch.epfl.isochrone.timetable.Date;
import ch.epfl.isochrone.timetable.Date.Month;
import ch.epfl.isochrone.timetable.FastestPathTree;
import ch.epfl.isochrone.timetable.Graph;
import ch.epfl.isochrone.timetable.SecondsPastMidnight;
import ch.epfl.isochrone.timetable.Service;
import ch.epfl.isochrone.timetable.Stop;
import ch.epfl.isochrone.timetable.TimeTable;
import ch.epfl.isochrone.timetable.TimeTableReader;

public final class IsochroneTL {
    private static final String OSM_TILE_URL = "http://b.tile.openstreetmap.org/";
    private static final int INITIAL_ZOOM = 11;
    private static final PointWGS84 INITIAL_POSITION = new PointWGS84(
            Math.toRadians(6.476), Math.toRadians(46.613));
    private static final String INITIAL_STARTING_STOP_NAME = "Lausanne-Flon";
    private static final int INITIAL_DEPARTURE_TIME = SecondsPastMidnight
            .fromHMS(6, 8, 0);
    private static final Date INITIAL_DATE = new Date(1, Month.OCTOBER, 2013);
    private static final int WALKING_TIME = 5 * 60;
    private static final double WALKING_SPEED = 1.25;
    private Point departSouris;
    private Point departFenetre;
    private Date date = INITIAL_DATE;
    private int departureTime = INITIAL_DEPARTURE_TIME;
    private Vector<Stop> stopsVector;
    private Graph g;
    private Set<Service> services;
    private TimeTableReader ttr;
    private Set<Stop> stops;
    private FastestPathTree fpt;
    private TiledMapComponent tiledMapComponent;
    private ColorTable ct;
    private IsochroneTileProvider isoTileProvider;
    private TransparentTileProvider transpTileProvider;
    private Stop startingStop;
    private TimeTable tt;

    private JComboBox<Stop> selectArrets;

    private Point positionSouris;
    private Timer timerForTrajet;
    private TrajetTileProvider ttp;

    private boolean animation = false;
    private Timer timer;
    private TimerTask timerTask;
    private int delay = 1000;

    // Selection de la date :
    private final SpinnerDateModel dateModel = new SpinnerDateModel();

    /**
     * Constructeur de IsochroneTL. Crée la première carte affichée et initie
     * les couleurs.
     * 
     * @throws IOException
     *             En cas d'erreur d'entrée-sortie par les readers.
     */
    public IsochroneTL() throws IOException {
        TileProvider bgTileProvider = new CachedTileProvider(
                new OSMTileProvider(new URL(OSM_TILE_URL)));
        tiledMapComponent = new TiledMapComponent(INITIAL_ZOOM);
        tiledMapComponent.add(bgTileProvider);

        ttr = new TimeTableReader("/time-table/");
        tt = ttr.readTimeTable();

        stops = tt.stops();
        stopsVector = new Vector<>(stops);
        Collections.sort(stopsVector);

        services = tt.servicesForDate(date);

        startingStop = null;

        for (Stop stop : stops)
            if (stop.name().equals(INITIAL_STARTING_STOP_NAME)) {
                startingStop = stop;
                break;
            }

        // Création du graph
        g = ttr.readGraphForServices(stops, services, WALKING_TIME,
                WALKING_SPEED);

        // Création du FastestPathTree de départ
        fpt = g.fastestPaths(startingStop, INITIAL_DEPARTURE_TIME);

        ArrayList<Color> lc = new ArrayList<Color>();

        // Création de la table de couleurs
        lc.add(new Color(255, 0, 0));
        lc.add(new Color(255, 127, 0));
        lc.add(new Color(255, 255, 0));
        lc.add(new Color(127, 255, 0));
        lc.add(new Color(0, 255, 0));
        lc.add(new Color(0, 127, 127));
        lc.add(new Color(0, 0, 255));
        lc.add(new Color(0, 0, 127));
        lc.add(new Color(0, 0, 0));

        ct = new ColorTable(WALKING_TIME, lc);

        // Ajoute de la tuile isochrone semi tranparente.
        isoTileProvider = new IsochroneTileProvider(fpt, ct, WALKING_SPEED);

        transpTileProvider = new TransparentTileProvider(0.5, isoTileProvider);

        tiledMapComponent.add(transpTileProvider);

        // Initialisation du Timer utilé avec le timerTaskTrajet
        timerForTrajet = new Timer();

    }

    /**
     * Construit le panneau de sélection de la date et l'heure
     * 
     * @return le JPanel construit.
     */
    @SuppressWarnings("deprecation")
    private JPanel createSelectionPanel() {
        JLabel etiquetteDepart = new JLabel("Départ:");
        JLabel etiquetteDateHeure = new JLabel("Date et heure:");
        final JCheckBox animationCheckBox = new JCheckBox("Animation", false);
        JLabel etiquetteVitesseAnim = new JLabel("Vitesse de l'animation:");

        // animationCheckBox est nécessaire à la mise en place du bonus
        // "Animation".
        animationCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                animation = ((JCheckBox) e.getSource()).isSelected();

                if (animation) {

                    timer = new Timer();
                    timerTask = new TimerTask() {

                        @Override
                        public void run() {

                            java.util.Date actualJavaDate = dateModel.getDate();
                            Date actualDate = new Date(actualJavaDate);
                            int spm = SecondsPastMidnight
                                    .fromJavaDate(actualJavaDate);
                            spm = spm + 60;

                            if (spm > 86400) {
                                actualDate = actualDate.relative(1);
                                spm = 1;
                            }

                            actualJavaDate = actualDate.toJavaDate();
                            actualJavaDate.setSeconds(SecondsPastMidnight
                                    .seconds(spm));
                            actualJavaDate.setMinutes(SecondsPastMidnight
                                    .minutes(spm));
                            actualJavaDate.setHours(SecondsPastMidnight
                                    .hours(spm));

                            // Mise à jour du SpinnerDateModel, qui gère la mise
                            // à jour de la carte isochrone
                            dateModel.setValue(actualJavaDate);

                        }

                    };
                    timer.schedule(timerTask, delay, delay);
                    animation = ((JCheckBox) e.getSource()).isSelected();
                } else {
                    timer.cancel();
                }
            }

        });

        // Selection du stop :
        selectArrets = new JComboBox<>(stopsVector);

        // Initialisation de la barre de choix
        selectArrets.setSelectedIndex(stopsVector.indexOf(startingStop));

        selectArrets.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                String newStartingStopName = selectArrets.getSelectedItem()
                        .toString();
                if (newStartingStopName.equals(startingStop.name()))
                    return;
                updateForNewStop(newStartingStopName);
            }
        });

        // initialisation de la barre de choix
        java.util.Date initialJavaDate = INITIAL_DATE.toJavaDate();
        initialJavaDate.setHours(SecondsPastMidnight
                .hours(INITIAL_DEPARTURE_TIME));
        initialJavaDate.setMinutes(SecondsPastMidnight
                .minutes(INITIAL_DEPARTURE_TIME));
        initialJavaDate.setSeconds(SecondsPastMidnight
                .seconds(INITIAL_DEPARTURE_TIME));
        dateModel.setValue(initialJavaDate);

        dateModel.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {

                Date newDate = new Date(dateModel.getDate());

                int newDepartureTime = SecondsPastMidnight
                        .fromJavaDate(dateModel.getDate());

                utilUpdate(newDate, newDepartureTime);
            }
        });
        final SpinnerNumberModel vitesseModel = new SpinnerNumberModel(1, 1,
                10, 1);
        vitesseModel.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                delay = (int) ((11 - vitesseModel.getNumber().doubleValue()) * 100);
                if (animation) {
                    animationCheckBox.doClick();
                    animationCheckBox.doClick();
                }
            }

        });
        final JSpinner selectDates = new JSpinner(dateModel);
        final JSpinner selectVitesse = new JSpinner(vitesseModel);

        final JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(25, 25));

        layeredPane.add(selectArrets);
        JPanel selectionPanel = new JPanel();
        selectionPanel.add(etiquetteDepart);
        selectionPanel.add(selectArrets);
        selectionPanel.add(etiquetteDateHeure);
        selectionPanel.add(selectDates);
        selectionPanel.add(etiquetteVitesseAnim);
        selectionPanel.add(selectVitesse);
        selectionPanel.add(animationCheckBox);
        return selectionPanel;
    }

    /**
     * Construit le panneau central contenant la carte isochrone.
     * 
     * @return
     */
    private JComponent createCenterPanel() {

        final JViewport viewPort = new JViewport();
        viewPort.setView(tiledMapComponent);
        PointOSM startingPosOSM = INITIAL_POSITION.toOSM(tiledMapComponent
                .getZoom());
        viewPort.setViewPosition(new Point(startingPosOSM.roundedX(),
                startingPosOSM.roundedY()));

        final JPanel copyrightPanel = createCopyrightPanel();

        final JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(400, 300));

        layeredPane.add(viewPort, new Integer(0));
        layeredPane.add(copyrightPanel, new Integer(1));

        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                final Rectangle newBounds = layeredPane.getBounds();
                viewPort.setBounds(newBounds);
                copyrightPanel.setBounds(newBounds);

                viewPort.revalidate();
                copyrightPanel.revalidate();
            }
        });

        layeredPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                departSouris = e.getLocationOnScreen();
                departFenetre = viewPort.getViewPosition();
            }
        });

        // Deplacement de la carte avec la souris
        layeredPane.addMouseMotionListener(new MouseMotionListener() {
            /*
             * (non-Javadoc)
             * 
             * @see
             * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event
             * .MouseEvent)
             */
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = (int) Math.round(e.getLocationOnScreen().getX());
                int y = (int) Math.round(e.getLocationOnScreen().getY());
                Point p = new Point((int) Math.round(departFenetre.getX() - x
                        + departSouris.getX()), (int) Math.round(departFenetre
                        .getY() - y + departSouris.getY()));
                viewPort.setViewPosition(p);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // TODO Auto-generated method stub
                // Rien à faire.
            }

        });

        // Zoom de la carte à la souris (molette)
        layeredPane.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int modif = e.getWheelRotation();

                departSouris = e.getPoint();

                Point jvp = viewPort.getViewPosition().getLocation();

                int newZoomLevel = tiledMapComponent.getZoom();

                PointOSM temp = new PointOSM(tiledMapComponent.getZoom(), jvp
                        .getX() + departSouris.getX(), jvp.getY()
                        + departSouris.getY());

                if (modif < 0) {
                    while (modif != 0) {
                        if (newZoomLevel < 19) {
                            newZoomLevel++;
                        }
                        modif++;
                    }
                } else {
                    while (modif != 0) {
                        if (newZoomLevel > 10) {
                            newZoomLevel--;
                        }
                        modif--;
                    }
                }

                temp = temp.atZoom(newZoomLevel);

                tiledMapComponent.setZoomLevel(newZoomLevel);
                Point temp2 = new Point(temp.roundedX() - departSouris.x, temp
                        .roundedY() - departSouris.y);
                viewPort.setViewPosition(temp2);

            }

        });

        // Affichage du plus court trajet après un arrêt de la souris de 750ms
        layeredPane.addMouseMotionListener(new MouseMotionListener() {

            TimerTask timerTaskTrajet = new TimerTask() {
                @Override
                public void run() {
                }
            };

            @Override
            public void mouseMoved(final MouseEvent e) {

                // Annulation de la TimerTask pour ne pas afficher le trajet
                // précédent.
                timerTaskTrajet.cancel();

                timerTaskTrajet = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            positionSouris = e.getPoint();
                            departFenetre = viewPort.getViewPosition();

                            Point p = new Point((int) Math.round(departFenetre
                                    .getX() + positionSouris.getX()),
                                    (int) Math.round(departFenetre.getY()
                                            + positionSouris.getY()));

                            PointOSM pOSM = new PointOSM(tiledMapComponent
                                    .getZoom(), p.getX(), p.getY());

                            Stop stopLePlusProche = fastestReachableStop(pOSM);

                            List<Stop> listeStop = fpt.pathTo(stopLePlusProche);

                            tiledMapComponent.removeTileProvider(ttp);

                            ttp = new TrajetTileProvider(listeStop, pOSM);

                            tiledMapComponent.add(ttp);

                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                };

                timerForTrajet.schedule(timerTaskTrajet, 750);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // TODO Auto-generated method stub

            }
        });

        // Selection de l'arrêt de départ le plus proche lorsque l'on applique
        // sur clic droit
        layeredPane.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO Auto-generated method stub
                if (e.getButton() == MouseEvent.BUTTON3) {
                    positionSouris = e.getPoint();
                    departFenetre = viewPort.getViewPosition();

                    Point p = new Point((int) Math.round(departFenetre.getX()
                            + positionSouris.getX()),
                            (int) Math.round(departFenetre.getY()
                                    + positionSouris.getY()));

                    PointOSM pOSM = new PointOSM(tiledMapComponent.getZoom(), p
                            .getX(), p.getY());

                    Stop stopLePlusProche = closestStop(pOSM);

                    updateForNewStop(stopLePlusProche.name());

                    // Mise A jour de la ComboBox d'arrêts
                    selectArrets.setSelectedIndex(stopsVector
                            .indexOf(stopLePlusProche));

                }

            }
        });

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(layeredPane, BorderLayout.CENTER);
        return centerPanel;
    }

    /**
     * Recherche le stop le plus proche d'un point OSM donné en paramètre
     * 
     * @param pOSM
     *            Le point d'ou on cherche le stop le plus proche
     * @return le stop le plus proche
     */
    private Stop closestStop(PointOSM pOSM) {
        Stop stopProche = startingStop;

        int i = Integer.MAX_VALUE;
        for (Stop s : stops) {
            if (s.position().distanceTo(pOSM.toWGS84()) < i) {

                if (!(fpt.arrivalTime(s) == SecondsPastMidnight.INFINITE)) {
                    i = (int) s.position().distanceTo(pOSM.toWGS84());
                    stopProche = s;

                }
            }
        }

        return stopProche;
    }

    /**
     * Recherche le stop le plus rapidement atteignable d'un point OSM donné en
     * paramètre
     * 
     * @param pOSM
     *            Le point d'ou on cherche le stop le plus proche
     * @return le stop atteingable en le moins de temps
     */
    private Stop fastestReachableStop(PointOSM pOSM) {
        Stop stopProche = startingStop;

        double j = SecondsPastMidnight.INFINITE;
        for (Stop s : stops) {
            double d = fpt.arrivalTime(s) - fpt.startingTime()
                    + s.position().distanceTo(pOSM.toWGS84()) / WALKING_SPEED;
            if (d < j) {
                j = d;
                stopProche = s;
            }

        }
        return stopProche;
    }

    /**
     * Recherche le nouveau stop dans la liste, puis appelle la méthode de mise
     * à jour avec le nouveau stop.
     * 
     * @param newStartingStopName
     */
    private void updateForNewStop(String newStartingStopName) {
        for (Stop stop : stops)
            if (stop.name().equals(newStartingStopName)) {
                startingStop = stop;
                break;
            }
        updateIso();
    }

    /**
     * Met à jour la carte isochrone en fonction des attributs statiques de
     * classe startingStop et departureTime. Ne gère pas le changement de date.
     */
    private void updateIso() {
        fpt = g.fastestPaths(startingStop, departureTime);
        tiledMapComponent.removeTileProvider(transpTileProvider);

        isoTileProvider = new IsochroneTileProvider(fpt, ct, WALKING_SPEED);

        transpTileProvider = new TransparentTileProvider(0.5, isoTileProvider);

        tiledMapComponent.removeTileProvider(ttp);

        tiledMapComponent.add(transpTileProvider);
    }

    /**
     * Gère le changement d'heure de départ, sans changement de date ou de stop
     * de départ.
     */
    private void updateForNewDepartureTime() {
        updateIso();
    }

    /**
     * Met à jour les services disponibles à la date de départ.
     */
    private void updateForNewDate() {

        services = tt.servicesForDate(date);

        try {
            g = ttr.readGraphForServices(stops, services, WALKING_TIME,
                    WALKING_SPEED);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        updateIso();
    }

    /**
     * Met a jour la date et l'heure en fonction des paramètres, utile pour
     * gérer les heures entre minuit et 4h du matin qui sont traitée comme
     * respectivement 24h et 28h.
     * 
     * @param actualDate
     *            Date réelle après minuit
     * @param spm
     *            Nombre de soncondes après minuit
     */
    private void utilUpdate(Date actualDate, int spm) {
        // Pour les heures comprises entre 0h et 4h :
        if (spm < 14400) {
            actualDate = actualDate.relative(-1);
            spm = spm + 86400;
        }

        if (actualDate.equals(date)) {
            if (spm == departureTime) {
                return;
            } else {
                departureTime = spm;
                updateForNewDepartureTime();
            }
        } else {
            date = actualDate;
            departureTime = spm;
            updateForNewDate();
        }
    }

    /**
     * Crée le panneau d'affichage du Copyright.
     * 
     * @return le JPanel correspondant.
     */
    private JPanel createCopyrightPanel() {
        Icon tlIcon = new ImageIcon(getClass().getResource(
                "/images/tl-logo.png"));
        String copyrightText = "Données horaires 2013. Source : Transports publics de la région lausannoise / Carte : © contributeurs d'OpenStreetMap";
        JLabel copyrightLabel = new JLabel(copyrightText, tlIcon,
                SwingConstants.CENTER);
        copyrightLabel.setOpaque(true);
        copyrightLabel.setForeground(new Color(1f, 1f, 1f, 0.6f));
        copyrightLabel.setBackground(new Color(0f, 0f, 0f, 0.4f));
        copyrightLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 5, 0));

        JPanel copyrightPanel = new JPanel(new BorderLayout());
        copyrightPanel.add(copyrightLabel, BorderLayout.PAGE_END);
        copyrightPanel.setOpaque(false);
        return copyrightPanel;
    }

    private void start() {
        JFrame frame = new JFrame("Isochrone TL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(createCenterPanel(), BorderLayout.CENTER);
        frame.getContentPane().add(createSelectionPanel(),
                BorderLayout.PAGE_START);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new IsochroneTL().start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}