<template>
  <v-menu
    v-model="menu"
    :close-on-content-click="false"
    offset="8"
    max-width="520"
  >
    <template #activator="{ props: menuProps }">
      <v-text-field
        v-bind="menuProps"
        v-model="q"
        class="ee-search"
        :class="inputClass"
        :variant="variant"
        :density="density"
        :loading="loading"
        :placeholder="placeholder"
        hide-details
        single-line
        prepend-inner-icon="mdi-magnify"
        @update:modelValue="onDebounced"
        @keydown.enter.prevent="runSearch"
        @focus="menu = true"
      />
    </template>

    <v-card rounded="lg">
      <v-list v-if="suggestions.length" density="comfortable">
        <v-list-item
          v-for="s in suggestions"
          :key="s.id"
          @click="select(s)"
          :title="`${s.prenom} ${s.nom}`"
        />
      </v-list>

      <v-card-text v-else class="text-medium-emphasis">
        Aucun résultat
      </v-card-text>
    </v-card>
  </v-menu>
</template>

<script setup>
import { ref } from "vue";
import { api } from "../services/api";

const props = defineProps({
  placeholder: { type: String, default: "Rechercher un élève, une classe..." },
  variant: { type: String, default: "outlined" }, // ou "solo-filled"
  density: { type: String, default: "comfortable" },
  inputClass: { type: String, default: "" },
});

const emit = defineEmits(["select"]);

const q = ref("");
const suggestions = ref([]);
const loading = ref(false);
const menu = ref(false);

let timer;

function onDebounced() {
  menu.value = true;
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
  q.value = `${item.prenom} ${item.nom}`;
  menu.value = false;
  emit("select", item);
}
</script>

<style scoped>
/* Style proche du React: input translucide sur gradient */
.ee-search :deep(.v-field) {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.95);
  transition: background 180ms ease, border-color 180ms ease;
}

.ee-search :deep(.v-field:hover) {
  background: rgba(255, 255, 255, 0.14);
  border-color: rgba(255, 255, 255, 0.28);
}

.ee-search :deep(.v-field--focused) {
  background: rgba(255, 255, 255, 0.18);
  border-color: rgba(255, 255, 255, 0.4);
}

/* placeholder + icône */
.ee-search :deep(input::placeholder) {
  color: rgba(255, 255, 255, 0.6);
}

.ee-search :deep(.v-field__prepend-inner .v-icon),
.ee-search :deep(.v-field__append-inner .v-icon) {
  color: rgba(255, 255, 255, 0.7);
}
</style>
