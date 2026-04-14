import java.util.ArrayList;
import java.util.List;

public class ExtratorRegras {

    public String extrair(String textoArvore, String classe) {
        List<String> regras = new ArrayList<>();
        List<String> caminho = new ArrayList<>();

        String[] linhas = textoArvore.split("\\R");
        for (String linhaBruta : linhas) {
            String linhaTrim = linhaBruta.trim();
            if (linhaTrim.isEmpty()) {
                continue;
            }
            if (linhaTrim.startsWith("Number of") || linhaTrim.startsWith("Size of")) {
                break;
            }
            if (!linhaTrim.contains(":") && !linhaTrim.contains("<") && !linhaTrim.contains(">") && !linhaTrim.contains("=")) {
                continue;
            }

            int nivel = contarNivel(linhaBruta);
            while (caminho.size() > nivel) {
                caminho.remove(caminho.size() - 1);
            }

            String linha = removerNivel(linhaBruta).trim();
            int posDoisPontos = linha.indexOf(':');

            if (posDoisPontos >= 0) {
                String condicaoFolha = linha.substring(0, posDoisPontos).trim();
                String saida = linha.substring(posDoisPontos + 1).trim();
                String classePrevista = saida;
                int posParenteses = saida.indexOf('(');
                if (posParenteses >= 0) {
                    classePrevista = saida.substring(0, posParenteses).trim();
                }

                List<String> condicoes = new ArrayList<>(caminho);
                if (!condicaoFolha.isEmpty()) {
                    condicoes.add(condicaoFolha);
                }

                regras.add("IF " + String.join(" AND ", condicoes) + " THEN " + classe + " = " + classePrevista);
            } else {
                caminho.add(linha);
            }
        }

        StringBuilder saida = new StringBuilder();
        for (int i = 0; i < regras.size(); i++) {
            saida.append("R").append(i + 1).append(": ").append(regras.get(i)).append("\n");
        }
        return saida.toString();
    }

    private int contarNivel(String linha) {
        int nivel = 0;
        String texto = linha;
        while (texto.startsWith("|")) {
            nivel++;
            if (texto.startsWith("|   ")) {
                texto = texto.substring(4);
            } else if (texto.startsWith("|  ")) {
                texto = texto.substring(3);
            } else if (texto.startsWith("| ")) {
                texto = texto.substring(2);
            } else {
                texto = texto.substring(1);
            }
        }
        return nivel;
    }

    private String removerNivel(String linha) {
        String texto = linha;
        while (texto.startsWith("|")) {
            if (texto.startsWith("|   ")) {
                texto = texto.substring(4);
            } else if (texto.startsWith("|  ")) {
                texto = texto.substring(3);
            } else if (texto.startsWith("| ")) {
                texto = texto.substring(2);
            } else {
                texto = texto.substring(1);
            }
        }
        return texto;
    }
}
