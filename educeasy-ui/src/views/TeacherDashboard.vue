<template>
  <section class="grid cols-1">
    <div class="card">
      <h3>Ma/mes classe(s)</h3>
      <div style="display: flex; gap: 0.5rem; margin-top: 0.25rem">
        <input
          class="input"
          v-model.number="classeId"
          placeholder="ID classe"
        />
        <button class="btn" @click="loadEleves">Charger élèves</button>
      </div>
      <ul>
        <li v-for="e in eleves" :key="e.id">{{ e.prenom }} {{ e.nom }}</li>
      </ul>
    </div>
  </section>
</template>

<script setup>
import { ref } from "vue";
import { api } from "../services/api";
const classeId = ref(1);
const eleves = ref([]);
async function loadEleves() {
  if (!classeId.value) return;
  const { data } = await api.get(`/api/classes/${classeId.value}/eleves`);
  eleves.value = data;
}
</script>
