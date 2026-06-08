package es.uji.ei1027.sgovi.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLEncoder;
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
import java.nio.charset.StandardCharsets;

@Service
public class TableViewService {
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern NON_SEARCH_CHARS = Pattern.compile("[^\\p{Alnum}]+");
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 20;

    public <T> List<T> apply(List<T> rows,
                             String query,
                             String sort,
                             String direction,
                             Map<String, Function<T, ?>> sorters,
                             List<Function<T, ?>> searchableFields) {
        List<T> result = filter(rows, query, searchableFields);
        sort(result, sort, direction, sorters);
        return paginate(result);
    }

    public void addState(Model model,
                         String action,
                         String query,
                         String sort,
                         String direction,
                         Map<String, String> sortOptions) {
        model.addAttribute("tableAction", action);
        model.addAttribute("tableQuery", clean(query));
        model.addAttribute("tableSort", selectedSort(sort, sortOptions));
        model.addAttribute("tableDir", "desc".equalsIgnoreCase(direction) ? "desc" : "asc");
        model.addAttribute("tableSortOptions", sortOptions);
        model.addAttribute("tableQueryEncoded", encode(clean(query)));
        model.addAttribute("tablePage", requestAttribute("tablePage", 1));
        model.addAttribute("tableSize", requestAttribute("tableSize", DEFAULT_PAGE_SIZE));
        model.addAttribute("tableTotalRows", requestAttribute("tableTotalRows", 0));
        model.addAttribute("tableTotalPages", requestAttribute("tableTotalPages", 1));
        model.addAttribute("tablePageStart", requestAttribute("tablePageStart", 0));
        model.addAttribute("tablePageEnd", requestAttribute("tablePageEnd", 0));
        model.addAttribute("tablePages", requestAttribute("tablePages", List.of(1)));
        model.addAttribute("tableHasPrevious", requestAttribute("tableHasPrevious", false));
        model.addAttribute("tableHasNext", requestAttribute("tableHasNext", false));
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
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
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

    private <T> List<T> paginate(List<T> rows) {
        int totalRows = rows.size();
        int size = pageSize();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalRows / size));
        int page = Math.min(Math.max(1, pageNumber()), totalPages);
        int from = Math.min((page - 1) * size, totalRows);
        int to = Math.min(from + size, totalRows);

        setRequestAttribute("tablePage", page);
        setRequestAttribute("tableSize", size);
        setRequestAttribute("tableTotalRows", totalRows);
        setRequestAttribute("tableTotalPages", totalPages);
        setRequestAttribute("tablePageStart", totalRows == 0 ? 0 : from + 1);
        setRequestAttribute("tablePageEnd", to);
        setRequestAttribute("tablePages", pageWindow(page, totalPages));
        setRequestAttribute("tableHasPrevious", page > 1);
        setRequestAttribute("tableHasNext", page < totalPages);

        return new ArrayList<>(rows.subList(from, to));
    }

    private List<Integer> pageWindow(int page, int totalPages) {
        int start = Math.max(1, page - 2);
        int end = Math.min(totalPages, page + 2);
        if (end - start < 4) {
            start = Math.max(1, end - 4);
            end = Math.min(totalPages, start + 4);
        }

        List<Integer> pages = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            pages.add(i);
        }
        return pages;
    }

    private int pageNumber() {
        return positiveIntParameter("page", 1);
    }

    private int pageSize() {
        int requestedSize = positiveIntParameter("size", DEFAULT_PAGE_SIZE);
        return requestedSize <= DEFAULT_PAGE_SIZE ? DEFAULT_PAGE_SIZE : MAX_PAGE_SIZE;
    }

    private int positiveIntParameter(String name, int fallback) {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return fallback;
        }
        String value = request.getParameter(name);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Math.max(1, Integer.parseInt(value));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private HttpServletRequest currentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return attributes.getRequest();
        }
        return null;
    }

    private void setRequestAttribute(String name, Object value) {
        HttpServletRequest request = currentRequest();
        if (request != null) {
            request.setAttribute(name, value);
        }
    }

    private Object requestAttribute(String name, Object fallback) {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return fallback;
        }
        Object value = request.getAttribute(name);
        return value == null ? fallback : value;
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

    private String selectedSort(String sort, Map<String, String> sortOptions) {
        String cleaned = clean(sort);
        if (!cleaned.isEmpty() && sortOptions.containsKey(cleaned)) {
            return cleaned;
        }
        return sortOptions.keySet().stream().findFirst().orElse("");
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
