<template>
  <v-menu
    v-model="menu"
    :close-on-content-click="false"
    :close-on-back="false"
    offset="8"
    max-width="520"
    eager
  >
    <template #activator="{ props: menuProps }">
      <v-text-field
        v-bind="menuProps"
        v-model="q"
        class="ee-search"
        :class="[inputClass, `ee-search--${theme}`]"
        :variant="variant"
        :density="density"
        :loading="loading"
        :placeholder="placeholder"
        hide-details
        single-line
        prepend-inner-icon="mdi-magnify"
        @update:modelValue="onDebounced"
        @keydown.enter.prevent="runSearch"
        @click="onClickField"
      >
        <template #append-inner v-if="q">
          <v-btn
            icon
            size="x-small"
            variant="text"
            @click="clearSearch"
            aria-label="Effacer"
          >
            <v-icon size="18">mdi-close</v-icon>
          </v-btn>
        </template>
      </v-text-field>
    </template>

    <v-card rounded="lg">
      <!-- History -->
      <div v-if="!q.trim() && history.length">
        <v-list-subheader>Recherches récentes</v-list-subheader>
        <v-list density="compact">
          <v-list-item
            v-for="(h, i) in history"
            :key="i"
            @click="loadFromHistory(h)"
          >
            <template #prepend>
              <v-icon size="18" color="grey">mdi-history</v-icon>
            </template>
            <v-list-item-title class="text-body-2">{{ h }}</v-list-item-title>
            <template #append>
              <v-btn
                icon
                size="x-small"
                variant="text"
                @click.stop="removeHistory(i)"
                aria-label="Retirer"
              >
                <v-icon size="16">mdi-close</v-icon>
              </v-btn>
            </template>
          </v-list-item>
        </v-list>
        <v-divider />
      </div>

      <!-- Empty state when no query and no history -->
      <v-card-text
        v-if="!q.trim() && !history.length"
        class="text-medium-emphasis text-center py-4"
      >
        <v-icon size="32" color="grey-lighten-1">mdi-magnify</v-icon>
        <div class="mt-2">Rechercher un élève...</div>
      </v-card-text>

      <!-- Results -->
      <v-list v-if="suggestions.length" density="comfortable">
        <v-list-item v-for="s in suggestions" :key="s.id" @click="select(s)">
          <template #prepend>
            <v-avatar size="32" :color="avatarColor(s.gender)">
              <span class="text-caption font-weight-bold">{{
                getInitials(s)
              }}</span>
            </v-avatar>
          </template>

          <v-list-item-title> {{ s.prenom }} {{ s.nom }} </v-list-item-title>
          <v-list-item-subtitle v-if="s.classroomName" class="text-caption">
            {{ s.classroomName }}
          </v-list-item-subtitle>

          <template #append>
            <div class="d-flex" style="gap: 4px">
              <v-btn
                icon
                size="x-small"
                variant="text"
                color="primary"
                @click.stop="$emit('quick-note', s)"
                aria-label="Ajouter une note rapide"
              >
                <v-icon size="18">mdi-note-plus-outline</v-icon>
              </v-btn>
              <v-btn
                icon
                size="x-small"
                variant="text"
                color="warning"
                @click.stop="$emit('quick-absence', s)"
                aria-label="Marquer absent"
              >
                <v-icon size="18">mdi-account-off-outline</v-icon>
              </v-btn>
            </div>
          </template>
        </v-list-item>
      </v-list>

      <v-card-text
        v-else-if="!loading && q.trim()"
        class="text-medium-emphasis text-center"
      >
        Aucun résultat
      </v-card-text>
    </v-card>
  </v-menu>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { api } from "../services/api";

const props = defineProps({
  placeholder: { type: String, default: "Rechercher un élève, une classe..." },
  variant: { type: String, default: "outlined" }, // ou "solo-filled"
  density: { type: String, default: "comfortable" },
  inputClass: { type: String, default: "" },
  theme: { type: String, default: "dark" }, // "dark" pour navbar, "light" pour dialog
});

const emit = defineEmits(["select", "quick-note", "quick-absence"]);

const q = ref("");
const suggestions = ref([]);
const loading = ref(false);
const menu = ref(false);
const history = ref([]);

const HISTORY_KEY = "educeasy_search_history";
const MAX_HISTORY = 5;

let timer;

onMounted(() => {
  loadHistory();
});

function loadHistory() {
  try {
    const stored = localStorage.getItem(HISTORY_KEY);
    if (stored) {
      history.value = JSON.parse(stored);
    }
  } catch (e) {
    console.error("Error loading search history:", e);
  }
}

function saveToHistory(term) {
  if (!term?.trim()) return;

  // Remove if already exists
  const filtered = history.value.filter((h) => h !== term);

  // Add to beginning
  history.value = [term, ...filtered].slice(0, MAX_HISTORY);

  try {
    localStorage.setItem(HISTORY_KEY, JSON.stringify(history.value));
  } catch (e) {
    console.error("Error saving search history:", e);
  }
}

function loadFromHistory(term) {
  q.value = term;
  runSearch();
}

function removeHistory(index) {
  history.value.splice(index, 1);
  try {
    localStorage.setItem(HISTORY_KEY, JSON.stringify(history.value));
  } catch (e) {
    console.error("Error updating search history:", e);
  }
}

function clearSearch() {
  q.value = "";
  suggestions.value = [];
  menu.value = false;
}

function onClickField() {
  menu.value = true;
  if (q.value.trim()) {
    runSearch();
  }
}

function onDebounced() {
  if (!menu.value) menu.value = true;
  if (timer) clearTimeout(timer);
  timer = setTimeout(runSearch, 220);
}

async function runSearch() {
  const term = q.value?.trim();
  if (!term) {
    suggestions.value = [];
    return;
  }

  loading.value = true;
  try {
    // Search pupils by name/firstname AND by classroom name
    const { data } = await api.get("/pupils/search", {
      params: { query: term },
    });
    suggestions.value = Array.isArray(data) ? data : [];
  } catch {
    suggestions.value = [];
  } finally {
    loading.value = false;
  }
}

function select(item) {
  const fullName = `${item.prenom} ${item.nom}`;
  saveToHistory(fullName);
  q.value = fullName;
  emit("select", item);
}

function highlightMatch(text) {
  if (!q.value?.trim() || !text) return text;

  const term = q.value.trim();
  const regex = new RegExp(`(${term})`, "gi");
  return text.replace(
    regex,
    '<mark style="background: #ffd54f; padding: 2px 0; border-radius: 2px;">$1</mark>',
  );
}

function getInitials(pupil) {
  const first = (pupil.prenom || "").charAt(0) || "";
  const last = (pupil.nom || "").charAt(0) || "";
  return (first + last || "?").toUpperCase();
}

function avatarColor(gender) {
  if (String(gender).toUpperCase() === "GIRL") return "pink";
  if (String(gender).toUpperCase() === "BOY") return "blue";
  return "grey";
}
</script>

<style scoped>
/* Theme DARK (navbar avec fond gradient) */
.ee-search--dark :deep(.v-field) {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.95);
  transition:
    background 180ms ease,
    border-color 180ms ease;
}

.ee-search--dark :deep(.v-field:hover) {
  background: rgba(255, 255, 255, 0.14);
  border-color: rgba(255, 255, 255, 0.28);
}

.ee-search--dark :deep(.v-field--focused) {
  background: rgba(255, 255, 255, 0.18);
  border-color: rgba(255, 255, 255, 0.4);
}

.ee-search--dark :deep(input::placeholder) {
  color: rgba(255, 255, 255, 0.6);
}

.ee-search--dark :deep(.v-field__prepend-inner .v-icon),
.ee-search--dark :deep(.v-field__append-inner .v-icon) {
  color: rgba(255, 255, 255, 0.7);
}

/* Theme LIGHT (dialog avec fond blanc) */
.ee-search--light :deep(.v-field) {
  background: #f5f5f5;
  border: 1px solid rgba(0, 0, 0, 0.12);
  color: #000;
  transition:
    background 180ms ease,
    border-color 180ms ease;
}

.ee-search--light :deep(.v-field:hover) {
  background: #eeeeee;
  border-color: rgba(0, 0, 0, 0.2);
}

.ee-search--light :deep(.v-field--focused) {
  background: #ffffff;
  border-color: #1976d2;
}

.ee-search--light :deep(input::placeholder) {
  color: rgba(0, 0, 0, 0.5);
}

.ee-search--light :deep(.v-field__prepend-inner .v-icon),
.ee-search--light :deep(.v-field__append-inner .v-icon) {
  color: rgba(0, 0, 0, 0.6);
}
</style>
