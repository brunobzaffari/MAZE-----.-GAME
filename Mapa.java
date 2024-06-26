import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Mapa {
    private List<String> mapa;
    private Map<Character, ElementoMapa> elementos;
    private Map<String, EBuracos> buracos;
    private Map<String, ElementoPortas> portas;
    private int x = 50; // Posição inicial X do personagem
    private int y = 50; // Posição inicial Y do personagem
    private final int TAMANHO_CELULA = 10; // Tamanho de cada célula do mapa
    // private boolean[][] areaRevelada; // Rastreia quais partes do mapa foram reveladas
    private final Color brickColor = new Color(153, 76, 0); // Cor marrom para tijolos
    private final Color vegetationColor = new Color(34, 139, 34); // Cor verde para vegetação
    private final Color portaColor = new Color(255,0,0); // Cor vermelho
    private final Color buracoColor = new Color(0,0,0);
    // private final int RAIO_VISAO = 5; // Raio de visão do personagem

    public Mapa(String arquivoMapa) {
        mapa = new ArrayList<>();
        elementos = new HashMap<>();
        portas = new HashMap<>();
        buracos = new HashMap<>();
        registraElementos();
        carregaMapa(arquivoMapa);
        registraOutros();
        startBuracos();
        // areaRevelada = new boolean[mapa.size()+1000][mapa.get(0).length()+1000];
        // atualizaCelulasReveladas();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTamanhoCelula() {
        return TAMANHO_CELULA;
    }

    public int getNumLinhas() {
        return mapa.size();
    }

    public int getNumColunas() {
        return mapa.get(0).length();
    }

    public ElementoMapa getElemento(int x, int y) {
        Character id = mapa.get(y).charAt(x);
        return elementos.get(id);
    
    }
    public ElementoPortas getPorta(int x, int y){
        Character id = mapa.get(y).charAt(x);
        if(id.equals('X')){
            String col = String.format("%02d", x);
            String lin = String.format("%02d", y);
            // Concatenando os números formatados
            String id_porta = lin + col;
            return portas.get(id_porta);
        }
        return null; // Retorna null se não for uma porta ou a porta não existir no mapa
    }
    public EBuracos getBuraco(int x, int y){
        Character id = mapa.get(y).charAt(x);
        if(id.equals('O')){
            String col = String.format("%02d", x);// x colina j
            String lin = String.format("%02d", y);// y linha i
            // Concatenando os números formatados
            String id_buraco = lin + col;
            return buracos.get(id_buraco);
        }
        return null; // Retorna null se não for uma porta ou a porta não existir no mapa
    }
    
    public char getC(int x, int y) {
        return mapa.get(y).charAt(x);
    }

    // public boolean estaRevelado(int x, int y) {
    //     return areaRevelada[y][x];
    // }

    // Move conforme enum Direcao
    public boolean move(Direcao direcao) {
        int dx = 0, dy = 0;

        switch (direcao) {
            case CIMA:
                dy = -TAMANHO_CELULA;
                break;
            case BAIXO:
                dy = TAMANHO_CELULA;
                break;
            case ESQUERDA:
                dx = -TAMANHO_CELULA;
                break;
            case DIREITA:
                dx = TAMANHO_CELULA;
                break;
            default:
                return false;
        }

        if (!podeMover(x + dx, y + dy)) {
            System.out.println("Nao pode mover");
            return false;
        }

        x += dx;
        y += dy;

        // Atualiza as células reveladas
        // atualizaCelulasReveladas();
        return true;
    }

    // Verifica se o personagem pode se mover para a próxima posição
    private boolean podeMover(int nextX, int nextY) {
        int mapX = nextX / TAMANHO_CELULA;
        int mapY = nextY / TAMANHO_CELULA - 1;

        if (mapa == null)
            return false;
        
        if (mapX >= 0 && mapX < mapa.get(0).length() && mapY >= 1 && mapY <= mapa.size()) {
            char id;

            try {
               id = mapa.get(mapY).charAt(mapX);
            } catch (StringIndexOutOfBoundsException e) {
                return false;
            }

            if (id == ' ')
                return true;
            if (id == 'O')
                return true;
            if (id == 'X'){
                // Formatando o primeiro número para ter sempre dois dígitos
                String col = String.format("%02d", mapX);
                String lin = String.format("%02d", mapY);
                // Concatenando os números formatados
                String id_p = lin + col;
                //System.out.println("Porta: " + id_p);
                ElementoPortas porta = portas.get(id_p);
                return porta.podeSerAtravessado();
            }

            ElementoMapa elemento = elementos.get(id);
            if (elemento != null) {
                //System.out.println("Elemento: " + elemento.getSimbolo() + " " + elemento.getCor());
                return elemento.podeSerAtravessado();
            }
        }
        return false;
    }

    public String interage() {
        //TODO: Implementar
        return "Interage";
    }

    public String ataca() {
        //TODO: Implementar
        return "Ataca";
    }

    private void carregaMapa(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                mapa.add(line);
                // Se character 'P' está contido na linha atual, então define a posição inicial do personagem
                if (line.contains("P")) {
                    x = line.indexOf('P') * TAMANHO_CELULA;
                    y = mapa.size() * TAMANHO_CELULA;
                    // Remove o personagem da linha para evitar que seja desenhado
                    mapa.set(mapa.size() - 1, line.replace('P', ' '));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // // Método para atualizar as células reveladas
    // private void atualizaCelulasReveladas() {
    //     if (mapa == null)
    //         return;
    //     for (int i = Math.max(0, y / TAMANHO_CELULA - RAIO_VISAO); i < Math.min(mapa.size(), y / TAMANHO_CELULA + RAIO_VISAO + 1); i++) {
    //         for (int j = Math.max(0, x / TAMANHO_CELULA - RAIO_VISAO); j < Math.min(mapa.get(i).length(), x / TAMANHO_CELULA + RAIO_VISAO + 1); j++) {
    //             areaRevelada[i][j] = true;
    //         }
    //     }
    // }

    // Registra os elementos do mapa
    private void registraElementos() {
        // Parede
        elementos.put('#', new Parede('▣', brickColor));
        // Vegetação
        elementos.put('V', new Vegetacao('♣', vegetationColor));
       // System.out.println("Elementos registrados");
        elementos.put('G', new Vegetacao('G', vegetationColor));
    }

    private void registraOutros() {// porta
        for (int i = 0; i < mapa.size(); i++) {
            for (int j = 0; j < mapa.get(i).length(); j++) {
                //System.out.println("Elemento: " + mapa.get(i).charAt(j));
                if (mapa.get(i).charAt(j) == 'X') {
                    // Formatando o primeiro número para ter sempre dois dígitos
                    String lin = String.format("%02d", i);
                    String col = String.format("%02d", j);
                    // Concatenando os números formatados
                    String id = lin + col; 
                    portas.put(id, new Porta('X', portaColor, j, i));
                    //System.out.println("Criada Porta: " + id + " " + j + " " + i);
                }
                if (mapa.get(i).charAt(j) == 'O') {
                    // Formatando o primeiro número para ter sempre dois dígitos
                    String lin = String.format("%02d", i);// y linha i
                    String col = String.format("%02d", j);// x colina j
                    // Concatenando os números formatados
                    String id = lin + col;
                    buracos.put(id, new EBuracos(2000, 'O', buracoColor, j, i));
                    System.out.println("Criada Buraco: " + id + " coluna j x " + j + " linha i y " + i);
                    System.out.println("Buraco: " + buracos.get(id).getSimbolo());
                }
            }
        }
    }
    private void startBuracos(){
        for (int i = 0; i < mapa.size(); i++) {
            for (int j = 0; j < mapa.get(0).length(); j++) {
                Character id = mapa.get(i).charAt(j);
                if(id.equals('O')){
                    String col = String.format("%02d", j);// x colina j
                    String lin = String.format("%02d", i);// y linha i
                    // Concatenando os números formatados
                    String id_buraco = lin + col;
                    EBuracos buraco = buracos.get(id_buraco);
                    buraco.start();
                    System.out.println("Buraco: " + buraco.getSimbolo() + " " + buraco.getCor());
                }
            }
        }
    }
}
