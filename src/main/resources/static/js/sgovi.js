(function () {
  const collator = new Intl.Collator("es", {
    numeric: true,
    sensitivity: "base"
  });

  function normalizeValue(value) {
    return (value || "")
      .replace(/\s+/g, " ")
      .trim();
  }

  function parseValue(value) {
    const text = normalizeValue(value);
    if (!text) {
      return { type: "empty", value: "" };
    }

    const dateMatch = text.match(/^(\d{4})-(\d{2})-(\d{2})(?:[ T](\d{2}):(\d{2})(?::(\d{2}))?)?/);
    if (dateMatch) {
      const time = Date.parse(text.replace(" ", "T"));
      if (!Number.isNaN(time)) {
        return { type: "date", value: time };
      }
    }

    const normalizedNumber = text
      .replace(/\./g, "")
      .replace(",", ".")
      .replace(/[^\d.-]/g, "");
    if (normalizedNumber && normalizedNumber !== "-" && !Number.isNaN(Number(normalizedNumber))) {
      return { type: "number", value: Number(normalizedNumber) };
    }

    return { type: "text", value: text };
  }

  function compareCells(a, b, direction) {
    const left = parseValue(a);
    const right = parseValue(b);

    if (left.type === "empty" && right.type !== "empty") return 1;
    if (right.type === "empty" && left.type !== "empty") return -1;

    let result;
    if (left.type === right.type && (left.type === "number" || left.type === "date")) {
      result = left.value - right.value;
    } else {
      result = collator.compare(String(left.value), String(right.value));
    }

    return direction === "desc" ? -result : result;
  }

  function ensureTableShell(table) {
    const parent = table.parentElement;
    if (!parent) return;

    if (parent.classList.contains("table-responsive") || parent.classList.contains("sgovi-table-shell")) {
      parent.classList.add("sgovi-table-shell");
      return;
    }

    const shell = document.createElement("div");
    shell.className = "table-responsive sgovi-table-shell";
    parent.insertBefore(shell, table);
    shell.appendChild(table);
  }

  function getCellText(row, index) {
    const cell = row.children[index];
    return cell ? normalizeValue(cell.textContent) : "";
  }

  function makeSortable(table) {
    const head = table.tHead;
    const body = table.tBodies[0];
    if (!head || !body) return;

    const headerRow = head.rows[0];
    if (!headerRow) return;

    const headers = Array.from(headerRow.cells);
    if (headers.length < 2) return;

    table.classList.add("sgovi-table");

    headers.forEach((header, columnIndex) => {
      const label = normalizeValue(header.textContent);
      if (!label || /acciones|acción|documento/i.test(label)) {
        header.classList.add("is-not-sortable");
        return;
      }

      header.classList.add("is-sortable");
      header.tabIndex = 0;
      header.setAttribute("role", "button");
      header.setAttribute("aria-sort", "none");
      header.setAttribute("title", "Ordenar por " + label);

      const sort = () => {
        const current = header.dataset.sortDirection === "asc" ? "desc" : "asc";
        headers.forEach((other) => {
          other.dataset.sortDirection = "";
          other.setAttribute("aria-sort", "none");
        });
        header.dataset.sortDirection = current;
        header.setAttribute("aria-sort", current === "asc" ? "ascending" : "descending");

        const rows = Array.from(body.rows).filter((row) => {
          const firstCell = row.cells[0];
          return firstCell && firstCell.colSpan < headers.length;
        });

        rows
          .map((row, originalIndex) => ({ row, originalIndex }))
          .sort((a, b) => {
            const result = compareCells(getCellText(a.row, columnIndex), getCellText(b.row, columnIndex), current);
            return result || a.originalIndex - b.originalIndex;
          })
          .forEach(({ row }) => body.appendChild(row));
      };

      header.addEventListener("click", sort);
      header.addEventListener("keydown", (event) => {
        if (event.key === "Enter" || event.key === " ") {
          event.preventDefault();
          sort();
        }
      });
    });
  }

  function enhanceTables() {
    document.querySelectorAll("table").forEach((table) => {
      ensureTableShell(table);
      makeSortable(table);
    });
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", enhanceTables);
  } else {
    enhanceTables();
  }
})();
