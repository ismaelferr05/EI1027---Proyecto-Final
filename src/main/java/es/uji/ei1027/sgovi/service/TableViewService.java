package es.uji.ei1027.sgovi.service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.text.Normalizer;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

@Service
public class TableViewService {
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern NON_SEARCH_CHARS = Pattern.compile("[^\\p{Alnum}]+");

    public <T> List<T> apply(List<T> rows,
                             String query,
                             String sort,
                             String direction,
                             Map<String, Function<T, ?>> sorters,
                             List<Function<T, ?>> searchableFields) {
        List<T> result = filter(rows, query, searchableFields);
        sort(result, sort, direction, sorters);
        return result;
    }

    public void addState(Model model,
                         String action,
                         String query,
                         String sort,
                         String direction,
                         Map<String, String> sortOptions) {
        model.addAttribute("tableAction", action);
        model.addAttribute("tableQuery", clean(query));
        model.addAttribute("tableSort", clean(sort));
        model.addAttribute("tableDir", "desc".equalsIgnoreCase(direction) ? "desc" : "asc");
        model.addAttribute("tableSortOptions", sortOptions);
    }

    public Map<String, String> options(String... keyLabels) {
        Map<String, String> options = new LinkedHashMap<>();
        for (int i = 0; i + 1 < keyLabels.length; i += 2) {
            options.put(keyLabels[i], keyLabels[i + 1]);
        }
        return options;
    }

    public <T> Map<String, Function<T, ?>> sorters(Object... keyExtractors) {
        Map<String, Function<T, ?>> sorters = new LinkedHashMap<>();
        for (int i = 0; i + 1 < keyExtractors.length; i += 2) {
            @SuppressWarnings("unchecked")
            Function<T, ?> extractor = (Function<T, ?>) keyExtractors[i + 1];
            sorters.put(String.valueOf(keyExtractors[i]), extractor);
        }
        return sorters;
    }

    @SafeVarargs
    public final <T> List<Function<T, ?>> fields(Function<T, ?>... fields) {
        return List.of(fields);
    }

    public String normalize(Object value) {
        if (value == null) {
            return "";
        }
        String text = Normalizer.normalize(String.valueOf(value), Normalizer.Form.NFD);
        text = DIACRITICS.matcher(text).replaceAll("");
        text = text.toLowerCase(Locale.ROOT)
                .replace('ç', 'c')
                .replace('·', ' ')
                .replace('ß', 's');
        String normalized = NON_SEARCH_CHARS.matcher(text).replaceAll(" ").trim().replaceAll("\\s+", " ");
        return (normalized + " " + statusAliases(normalized)).trim();
    }

    private String statusAliases(String normalized) {
        StringBuilder aliases = new StringBuilder();
        if (normalized.contains("approved") || normalized.contains("accepted")) {
            aliases.append(" aprobado aceptado");
        }
        if (normalized.contains("rejected")) {
            aliases.append(" rechazado");
        }
        if (normalized.contains("pending")) {
            aliases.append(" pendiente");
        }
        if (normalized.contains("in review")) {
            aliases.append(" en revision revision");
        }
        if (normalized.contains("in progress")) {
            aliases.append(" en progreso progreso");
        }
        if (normalized.contains("active") || normalized.contains("activo")) {
            aliases.append(" activo active");
        }
        if (normalized.contains("finished") || normalized.contains("finalizado")) {
            aliases.append(" finalizado finished");
        }
        if (normalized.contains("cancelled") || normalized.contains("canceled")) {
            aliases.append(" cancelado cancelled");
        }
        return aliases.toString();
    }

    private <T> List<T> filter(List<T> rows, String query, List<Function<T, ?>> fields) {
        String normalizedQuery = normalize(query);
        if (normalizedQuery.isBlank()) {
            return new ArrayList<>(rows);
        }

        String[] terms = normalizedQuery.split("\\s+");
        return rows.stream()
                .filter(row -> matches(row, fields, terms))
                .toList();
    }

    private <T> boolean matches(T row, List<Function<T, ?>> fields, String[] terms) {
        String searchable = fields.stream()
                .map(field -> safeApply(field, row))
                .filter(Objects::nonNull)
                .map(this::normalize)
                .reduce("", (left, right) -> left + " " + right);

        for (String term : terms) {
            if (!termMatches(searchable, term)) {
                return false;
            }
        }
        return true;
    }

    private boolean termMatches(String searchable, String term) {
        if (searchable.contains(term)) {
            return true;
        }
        for (String word : searchable.split("\\s+")) {
            if (word.length() >= 5 && term.length() >= 4 && levenshteinAtMostOne(word, term)) {
                return true;
            }
        }
        return false;
    }

    private boolean levenshteinAtMostOne(String left, String right) {
        if (Math.abs(left.length() - right.length()) > 1) {
            return false;
        }
        int i = 0;
        int j = 0;
        int edits = 0;
        while (i < left.length() && j < right.length()) {
            if (left.charAt(i) == right.charAt(j)) {
                i++;
                j++;
            } else if (++edits > 1) {
                return false;
            } else if (left.length() > right.length()) {
                i++;
            } else if (right.length() > left.length()) {
                j++;
            } else {
                i++;
                j++;
            }
        }
        return edits + (left.length() - i) + (right.length() - j) <= 1;
    }

    private <T> void sort(List<T> rows, String sort, String direction, Map<String, Function<T, ?>> sorters) {
        Function<T, ?> extractor = sorters.get(sort);
        if (extractor == null) {
            extractor = sorters.values().stream().findFirst().orElse(null);
        }
        if (extractor == null) {
            return;
        }

        Function<T, ?> selectedExtractor = extractor;
        Comparator<T> comparator = Comparator.comparing(
                row -> comparableValue(safeApply(selectedExtractor, row)),
                Comparator.nullsLast(Comparator.naturalOrder())
        );
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }
        rows.sort(comparator);
    }

    private Object safeApply(Function<?, ?> extractor, Object row) {
        try {
            @SuppressWarnings("unchecked")
            Function<Object, ?> function = (Function<Object, ?>) extractor;
            return function.apply(row);
        } catch (RuntimeException e) {
            return null;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Comparable comparableValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof Temporal || value instanceof Boolean) {
            return (Comparable) value;
        }
        if (value instanceof Comparable comparable && !(value instanceof String)) {
            return comparable;
        }
        return normalize(value);
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
