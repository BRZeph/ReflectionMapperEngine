package brzeph.spring.java_motordinamico_demo.reflectionEngine.core.mapper;

import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.exceptions.MergeEngineException;
import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.mapper.utils.impl.ReflectionMapperEngine;

import java.lang.annotation.Annotation;

import static brzeph.spring.java_motordinamico_demo.reflectionEngine.core.mapper.utils.impl.ReflectionMapperEngine.mergeIntoThirdObject;

public class Mapper {
    /*
    map to map -> devolve um JSON mapeado.
    map to object -> devolve um objeto mapeado.
    merge for x -> realiza merge entre 2 objetos e retorna o terceiro priorizando os dados preenchidos do segundo.
     */
    /**
     * Executa o mapeamento de leitura de um objeto, filtrando os seus campos com base na anotação annotationClass.
     * <p>
     * Este método é utilizado para preparar um JSON, incluindo apenas os campos
     * do objeto original que estão anotados com @[annotationClass].
     * Retorna um Map com os campos autorizados, o qual pode ser diretamente serializado em JSON pelo Jackson.
     *
     * @param source Objeto de origem completo (ex.: entidade carregada do banco de dados).
     * @param annotationClass classe da annotation a ser mapeada (ex.: Read.class).
     * @return Mapa contendo apenas os campos anotados com @Read.
     */
    public static Object mapToMapForX(Object source, Class<? extends Annotation> annotationClass) {
        return ReflectionMapperEngine.map(source, source.getClass(), annotationClass, true);
    }

    /**
     * Executa o mapeamento de leitura de um objeto, filtrando os seus campos com base na anotação annotationClass.
     * <p>
     * Este método é utilizado para preparar um objeto, incluindo apenas os campos
     * do objeto original que estão anotados com @[annotationClass].
     * Retorna um Map com os campos autorizados, o qual pode ser diretamente serializado em JSON pelo Jackson.
     *
     * @param source Objeto de origem completo (ex.: entidade carregada do banco de dados).
     * @param annotationClass classe da annotation a ser mapeada (ex.: Read.class).
     * @return Mapa contendo apenas os campos anotados com @Read.
     */
    public static Object mapToObjectForX(Object source, Class<? extends Annotation> annotationClass) {
        return ReflectionMapperEngine.map(source, source.getClass(), annotationClass, false);
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
    public static <T> T mergeForX(T base, T update, Class<? extends Annotation> context) {
        return ReflectionMapperEngine.mergeWithAnnotation(base, update, context);
    }


    /**
     * Mescla dois objetos complementares numa terceira instância de classe alvo, com validação total.
     * <p>
     * Este método assume que todos os campos da classe de destino (resultClass) existem em pelo menos
     * um dos dois objetos de origem (obj1 ou obj2), e que todos os campos de origem estão representados
     * no objeto de destino.
     * </p>
     * <p>
     * A correspondência é feita por nome exato dos campos (case-sensitive). Campos duplicados (existentes
     * em ambas as origens) são preferencialmente extraídos de {@code obj1}.
     * </p>
     *
     * @param obj1        Primeiro objeto de origem (ex: entidade principal).
     * @param obj2        Segundo objeto de origem (ex: entidade complementar).
     * @param resultClass Classe da instância de resultado que deve conter a união dos campos.
     * @param <A>         Tipo do primeiro objeto.
     * @param <B>         Tipo do segundo objeto.
     * @param <R>         Tipo do objeto de resultado.
     * @return Uma instância de {@code resultClass} com todos os campos preenchidos a partir de {@code obj1} e {@code obj2}.
     *
     * @throws MergeEngineException Se:
     *                              <ul>
     *                                <li>Algum campo da {@code resultClass} não existir em {@code obj1} nem {@code obj2}.</li>
     *                                <li>Algum campo de {@code obj1} ou {@code obj2} não existir em {@code resultClass}.</li>
     *                                <li>Algum erro de reflexão ocorrer durante a cópia.</li>
     *                              </ul>
     */
    public static <A, B, R> R complexMerge(A obj1, B obj2, Class<R> resultClass) {
        return mergeIntoThirdObject(obj1, obj2, resultClass); // Utilizar para juntar headers complementares.
    }
}
