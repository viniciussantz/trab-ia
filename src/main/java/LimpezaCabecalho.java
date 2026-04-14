import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LimpezaCabecalho {

    public static File preparar(File arquivoOriginal) throws IOException {
        List<String> linhas = Files.readAllLines(arquivoOriginal.toPath(), StandardCharsets.UTF_8);
        if (linhas.isEmpty()) {
            return arquivoOriginal;
        }

        String cabecalhoOriginal = linhas.get(0);
        String cabecalhoSemBom = cabecalhoOriginal.replace("\uFEFF", "");

        String[] colunas = cabecalhoSemBom.split(",", -1);
        Map<String, Integer> usados = new HashMap<>();

        for (int i = 0; i < colunas.length; i++) {
            String nome = colunas[i].trim();
            nome = nome.replaceAll("[^A-Za-z0-9]+", "_");
            nome = nome.replaceAll("_+", "_");

            if (nome.startsWith("_")) {
                nome = nome.substring(1);
            }
            if (nome.endsWith("_")) {
                nome = nome.substring(0, nome.length() - 1);
            }
            if (nome.isBlank()) {
                nome = "attr_" + (i + 1);
            }

            int qtd = usados.getOrDefault(nome, 0) + 1;
            usados.put(nome, qtd);
            if (qtd > 1) {
                nome = nome + "_" + qtd;
            }

            colunas[i] = nome;
        }

        String cabecalhoLimpo = String.join(",", colunas);
        boolean alterou = !cabecalhoLimpo.equals(cabecalhoSemBom) || !cabecalhoOriginal.equals(cabecalhoSemBom);
        if (!alterou) {
            return arquivoOriginal;
        }

        linhas.set(0, cabecalhoLimpo);
        File arquivoTemp = File.createTempFile("weka_limpo_", ".csv");
        arquivoTemp.deleteOnExit();
        Files.write(arquivoTemp.toPath(), linhas, StandardCharsets.UTF_8);
        return arquivoTemp;
    }
}
