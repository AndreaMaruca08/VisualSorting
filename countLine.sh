#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(pwd)"

# Moduli principali: tutte le directory di primo livello che hanno una src
modules=()
while IFS= read -r -d '' dir; do
  modules+=("$dir")
done < <(find "$ROOT_DIR" -mindepth 1 -maxdepth 1 -type d -print0)

tmp_files="$(mktemp)"
tmp_module_counts="$(mktemp)"
trap 'rm -f "$tmp_files" "$tmp_module_counts"' EXIT

total_lines=0

# Trova tutti i .java, conta le righe e salva:
# <righe>\t<modulo>\t<file>
for module_path in "${modules[@]}"; do
  module_name="$(basename "$module_path")"

  while IFS= read -r -d '' file; do
    if [[ "$file" == *.java ]]; then
      lines="$(wc -l < "$file" | tr -d ' ')"
      rel_file="${file#"$ROOT_DIR"/}"
      echo -e "${lines}\t${module_name}\t${rel_file}" >> "$tmp_files"
      total_lines=$((total_lines + lines))
      echo -e "${module_name}\t${lines}" >> "$tmp_module_counts"
    fi
  done < <(find "$module_path" -type f -name "*.java" -print0)
done

echo "=== FILE .JAVA ORDINATI PER NUMERO DI RIGHE (crescente) ==="
sort -n "$tmp_files" | awk -F'\t' '
{
    printf "%8s  %-15s  %s\n", $1, $2, $3
}
'

echo
echo "=== TOTALE RIGHE ==="
echo "$total_lines"

echo
echo "=== PERCENTUALE RIGHE PER MODULO ==="
awk -F'\t' -v total="$total_lines" '
{
    module[$1] += $2
}
END {
    for (m in module) {
        pct = (total > 0) ? (module[m] * 100 / total) : 0
        printf "%-15s  %8d righe  %6.2f%%\n", m, module[m], pct
    }
}
' "$tmp_module_counts" | sort