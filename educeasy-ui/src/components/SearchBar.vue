<template>
  <div style="position: relative">
    <input
      class="nav-input"
      v-model="q"
      type="search"
      placeholder="Rechercher un élève..."
      @input="onInput"
      @keydown.enter.prevent="runSearch"
    />
    <ul
      v-if="suggestions.length && showList"
      class="card"
      style="
        position: absolute;
        top: 110%;
        right: 0;
        width: min(520px, 90vw);
        max-height: 300px;
        overflow: auto;
      "
    >
      <li
        v-for="s in suggestions"
        :key="s.id"
        style="padding: 0.45rem 0.5rem; cursor: pointer"
        @click="select(s)"
      >
        {{ s.prenom }} {{ s.nom }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { api } from "../services/api";

const emit = defineEmits(["select"]);
const q = ref("");
const suggestions = ref([]);
const showList = ref(false);
let timer;

function onInput() {
  showList.value = true;
  if (timer) clearTimeout(timer);
  timer = setTimeout(runSearch, 250);
}

async function runSearch() {
  if (!q.value.trim()) {
    suggestions.value = [];
    return;
  }
  try {
    const { data } = await api.get("/pupils", {
      params: { query: q.value },
    });
    suggestions.value = data;
  } catch {
    suggestions.value = [];
  }
}

function select(item) {
  showList.value = false;
  q.value = `${item.prenom} ${item.nom}`;
  emit("select", item);
}
</script>
