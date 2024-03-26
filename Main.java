import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    static String zbiorPodstawowy = "";
    static String zbiorPrzeciwny = "";

    public static void main(String[] args) {


        int iloscParametrow = 0;
        int iloscParametrowTestowych = 0;
        Map<String, List<Double>> mapaRodzajow = new HashMap<>();
        Map<String, List<Double>> mapaRodzajowTestowych = new HashMap<>();

        try {

            if (args.length == 0) {
                System.out.println("Błędna ścieżka");
                System.exit(1);
            }

            Scanner scanner = new Scanner(new File(args[0]));

            if (!scanner.hasNextLine()) {
                System.out.println("Plik z danymi jest pusty");
                System.exit(2);
            } else {
                while (scanner.hasNextDouble()) {
                    scanner.nextDouble();
                    iloscParametrow++;
                }

            }
            scanner.close();
            Scanner scannerPodstawowy = new Scanner(new File(args[0]));
            mapaRodzajow = tworzenieMapyWartosci(scannerPodstawowy, iloscParametrow, mapaRodzajow);

            scannerPodstawowy.close();
        } catch (IOException e) {
            System.out.println("Błąd odczytu danych");
        }


        try {


            if (args.length <= 1) {
                System.out.println("Błędna ścieżka do pliku TESTOWEGO");
                System.exit(3);
            }

            Scanner scanner = new Scanner(new File(args[1]));

            if (!scanner.hasNextLine()) {
                System.out.println("Plik z danymi do testu jest pusty");
                System.exit(4);
            } else {
                while (scanner.hasNextDouble()) {
                    scanner.nextDouble();
                    iloscParametrowTestowych++;
                }

            }

            if (iloscParametrow != iloscParametrowTestowych) {
                System.out.println("Niekompatybilna ilość parametrów między plikami");
                System.exit(5);
            }
            scanner.close();
            Scanner scannerPodstawowy = new Scanner(new File(args[1]));


            mapaRodzajowTestowych = tworzenieMapyWartosci(scannerPodstawowy, iloscParametrowTestowych, mapaRodzajowTestowych);
            scannerPodstawowy.close();


        } catch (IOException e) {
            System.out.println("Błąd odczytu danych z pliku TESTOWEGO");
        }


        int poprawnieWytypowane = 0;
        int blednieWytypowane = 0;
        Per per = new Per(iloscParametrow);
        boolean czyPoprawny;

        for (int j = 0; j < 9; j++) {
            for (String s : mapaRodzajow.keySet()) {
                int przeskok = 0;
                List<Double> tmp = new ArrayList<>();
                for (double dd : mapaRodzajow.get(s)) {
                    tmp.add(dd);
                    //System.out.print(dd + ", ");
                    if (przeskok == iloscParametrow - 1) {
                        //System.out.println();
                        czyPoprawny = per.decyzja(tmp);
                        // System.out.println(listaOdleglosci);
                        if (czyPoprawny) {
                            if (!s.equals(zbiorPodstawowy)) {
                                while (per.decyzja(tmp)) {
                                  //  System.out.println("Potrzebna kalibracja z: " + per);
                                    per.kalibracja(tmp, 1, 0);
                                  //  System.out.println("Po kalibracji: " + per);
                                }
                            }
                        } else {
                            if (!s.equals(zbiorPrzeciwny)) {
                                while (!per.decyzja(tmp)) {
                                  //  System.out.println("Potrzebna kalibracja z: " + per);
                                    per.kalibracja(tmp, 0, 1);
                                 //   System.out.println("Po kalibracji: " + per);

                                }
                            }
                        }
                        przeskok = 0;
                        tmp.clear();
                    } else {
                        przeskok++;
                    }
                }
            }
        }


        int skok = 0;
        boolean wynik;
        for (String s : mapaRodzajowTestowych.keySet()) {
            List<Double> listaTmp = new ArrayList<>();

            for (Double d : mapaRodzajowTestowych.get(s)) {
                listaTmp.add(d);

                if (skok == iloscParametrow - 1) {
                    wynik = per.decyzja(listaTmp);
                    //System.out.println(listaTmp);
                    if (wynik) {
                        if (s.equals(zbiorPodstawowy)) {
                            poprawnieWytypowane++;
                        } else {
                            blednieWytypowane++;
                            System.out.println("Błąd: " + s + listaTmp);

                        }
                    } else {
                        if (s.equals(zbiorPrzeciwny)) {
                            poprawnieWytypowane++;
                        } else {
                            System.out.println("Błąd: " + s + listaTmp);
                            blednieWytypowane++;
                        }
                    }
                    skok = 0;
                    listaTmp.clear();
                } else {
                    skok++;
                }

            }

        }


        System.out.println(poprawnieWytypowane + "/" + (blednieWytypowane + poprawnieWytypowane));
        System.out.println((poprawnieWytypowane/(blednieWytypowane + poprawnieWytypowane)) * 100 + "%");

        Scanner scanner = new Scanner(System.in);

        int odpowiedz = 0;
        List<Double> wpisaneParametry = new ArrayList<>();
        while (true) {

            if (odpowiedz != 1 && odpowiedz != 2) {
                System.out.println("Czy chcesz podać nowe wartosci ? (tak -> 1, nie -> 2)");
                odpowiedz = sprawdzeniePoprawnosciDecyzji(scanner);
            } else {
                if (odpowiedz == 2) {
                    System.out.println("Poprawnie zakończono");
                    System.exit(6);
                }

                for (int i = 0; i < iloscParametrow; i++) {
                    System.out.println("Podaj: " + (i + 1) + " element");
                    wpisaneParametry.add(sprawdzeniePoprawnosciWpisanychDanych(scanner));
                }

                if (per.decyzja(wpisaneParametry)) {
                    System.out.println("Program wytypował: " + zbiorPodstawowy);
                } else {
                    System.out.println("Program wytypował: " + zbiorPrzeciwny);
                }
                wpisaneParametry.clear();
                odpowiedz = 0;
            }

        }

    }


    public static int sprawdzeniePoprawnosciDecyzji(Scanner scanner) {
        int tmp = 0;
        while (!scanner.hasNextInt()) {
            scanner.next();
        }
        tmp = scanner.nextInt();
        return tmp;
    }


    public static Double sprawdzeniePoprawnosciWpisanychDanych(Scanner scanner) {
        double tmp = 0.0;
        while (!scanner.hasNextDouble()) {
            scanner.next();
        }
        tmp = scanner.nextDouble();
        return tmp;
    }


    public static Map<String, List<Double>> tworzenieMapyWartosci(Scanner scannerPodstawowy, int iloscParametrow, Map<String, List<Double>> mapaRodzajow) {

        boolean czyUstaonoZbiorPodstawowy = true;

        while (scannerPodstawowy.hasNextLine()) {
            if (!scannerPodstawowy.hasNextDouble()) {
                break;
            }
            List<Double> listaTymczasowa = new ArrayList<>();
            for (int i = 0; i < iloscParametrow; i++) {

                listaTymczasowa.add(scannerPodstawowy.nextDouble());

            }
            String nazwa = scannerPodstawowy.nextLine();
            char[] nazwaPodzial = nazwa.toCharArray();
            String nazwaOstateczna = "";
            boolean czyPoczatek = true;
            int poczatek = 0;
            int koniec = 0;
            for (int i = 0; i < nazwaPodzial.length; i++) {
                if ((nazwaPodzial[i] >= 'a' && nazwaPodzial[i] <= 'z') || (nazwaPodzial[i] >= 'A' && nazwaPodzial[i] <= 'Z') || nazwaPodzial[i] == '-') {
                    if (czyPoczatek) {
                        poczatek = i;
                        czyPoczatek = false;
                    }

                    koniec++;

                }

            }
            nazwaOstateczna = new String(nazwaPodzial, poczatek, koniec);
            if (czyUstaonoZbiorPodstawowy) {
                czyUstaonoZbiorPodstawowy = false;
                zbiorPodstawowy = nazwaOstateczna;
                zbiorPrzeciwny = "NIE-" + nazwaOstateczna;
                mapaRodzajow.put(zbiorPodstawowy, new ArrayList<>());
                mapaRodzajow.put(zbiorPrzeciwny, new ArrayList<>());
            }

            if (nazwaOstateczna.equals(zbiorPodstawowy)) {
                // System.out.println("Dodano : " + listaTymczasowa);
                mapaRodzajow.get(zbiorPodstawowy).addAll(listaTymczasowa);
            } else {
                //System.out.println("Dodano : " + listaTymczasowa);
                mapaRodzajow.get(zbiorPrzeciwny).addAll(listaTymczasowa);
            }

            listaTymczasowa.clear();
        }

        return mapaRodzajow;
    }


}

class Per {

    public static List<Double> wartosciBramki = new ArrayList<>();
    public static double prog = 0;

    public double ostatniWynik;

    public Per(int iloscElementow) {
        for (int i = 0; i < iloscElementow; i++) {
            wartosciBramki.add(1.0);
        }
    }

    @Override
    public String toString() {
        return wartosciBramki + ",    " + prog;
    }

    public boolean decyzja(List<Double> d) {
        double wynik = 0;
        int i = 0;
        for (Double wartosci : wartosciBramki) {
            wynik += wartosci * d.get(i);
            i++;
        }
        ostatniWynik = wynik;
        return prog <= wynik;
    }

    public void kalibracja(List<Double> lista, double otrzymanyWynik, double oczekiwanyWynik) {
        List<Double> tmp = new ArrayList<>();
        int i = 0;
        for (double d : wartosciBramki) {
            tmp.add(d + ((oczekiwanyWynik - otrzymanyWynik) * lista.get(i)));
            i++;
        }
        wartosciBramki = tmp;
    }

}


