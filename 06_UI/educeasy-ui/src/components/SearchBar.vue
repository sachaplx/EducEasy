<template>
  <div class="search-anchor">
    <v-text-field
      v-model="q"
      class="nav-search"
      variant="outlined"
      density="comfortable"
      hide-details
      :loading="loading"
      placeholder="Rechercher un élève…"
      append-inner-icon="mdi-magnify"
      @update:modelValue="onDebounced()"
      @keydown.enter.prevent="runSearch"
      @focus="menu = true"
    />
    <v-menu
      v-model="menu"
      activator=".nav-search"
      :close-on-content-click="false"
      offset="8"
    >
      <v-card max-width="520">
        <v-list v-if="suggestions.length">
          <v-list-item
            v-for="s in suggestions"
            :key="s.id"
            @click="select(s)"
            :title="`${s.prenom} ${s.nom}`"
          />
        </v-list>
        <v-card-text v-else class="text-medium-emphasis"
          >Aucun résultat</v-card-text
        >
      </v-card>
    </v-menu>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { api } from "../services/api";

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
    suggestions.value = data;
  } catch {
    suggestions.value = [];
  } finally {
    loading.value = false;
  }
}

const emit = defineEmits(["select"]);
function select(item) {
  q.value = `${item.prenom} ${item.nom}`;
  menu.value = false;
  emit("select", item);
}
</script>

<style scoped>
.search-anchor {
  position: relative;
}
.nav-search {
  min-width: 360px;
  max-width: 42vw;
}
@media (max-width: 680px) {
  .nav-search {
    min-width: 220px;
    max-width: 60vw;
  }
}
</style>
