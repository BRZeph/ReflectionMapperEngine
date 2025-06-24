package brzeph.spring.java_motordinamico_demo.motor.core.mapper.impl;

import brzeph.spring.java_motordinamico_demo.motor.core.annotation.apiCallType.Read;
import brzeph.spring.java_motordinamico_demo.motor.core.annotation.apiCallType.Update;

public class Mapper {
    /**
     * Executa o mapeamento de leitura de um objeto, filtrando os seus campos com base na anotação @Read.
     * <p>
     * Este método é utilizado para preparar a resposta de saída (response body), incluindo apenas os campos
     * do objeto original que estão anotados com @Read.
     * Retorna um Map com os campos autorizados, o qual pode ser diretamente serializado em JSON pelo Jackson.
     *
     * @param source Objeto de origem completo (ex.: entidade carregada do banco de dados).
     * @return Mapa contendo apenas os campos anotados com @Read.
     */
    public static Object mapForRead(Object source) {
        return ReflectionMapperEngine.map(source, source.getClass(), Read.class, true);
    }

    /**
     * Realiza o merge dinâmico entre dois objetos da mesma classe, considerando apenas os campos anotados com @Update.
     * <p>
     * - Os campos anotados com @Update presentes no objeto `update` (segundo argumento) e que não estão nulos,
     *   sobrescrevem os valores correspondentes no objeto `base` (primeiro argumento).
     * - Campos com @Update que possuem valor null em `update` mantém o valor existente em `base`.
     * - Campos não anotados com @Update são ignorados e permanecem inalterados.
     * - Suporta mapeamento recursivo para atributos aninhados (nested POJOs).
     *
     * @param base Objeto de origem original (ex.: carregado do banco de dados).
     * @param update Objeto de atualização (ex.: recebido no request PUT).
     * @param <T> Tipo genérico do objeto.
     * @return Novo objeto resultante com o merge aplicado.
     */
    public static <T> T mergeForUpdate(T base, T update) {
        return ReflectionMapperEngine.mergeWithAnnotation(base, update, Update.class);
    }
}
