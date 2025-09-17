<template>
  <section class="grid cols-2">
    <div class="card">
      <h3>Écoles</h3>
      <button class="btn" @click="loadEcoles">Actualiser</button>
      <ul>
        <li v-for="e in ecoles" :key="e.id">
          <strong>{{ e.nom }}</strong> — {{ e.ville || "N/C" }}
        </li>
      </ul>
    </div>

    <div class="card">
      <h3>Classes par école</h3>
      <div v-if="ecoles.length === 0">Charge les écoles pour commencer.</div>
      <div v-else>
        <label>École:</label>
        <select class="input" v-model="selectedEcoleId">
          <option v-for="e in ecoles" :key="e.id" :value="e.id">
            {{ e.nom }}
          </option>
        </select>
        <button class="btn" style="margin-top: 0.5rem" @click="loadClasses">
          Voir classes
        </button>
        <ul>
          <li v-for="c in classes" :key="c.id">
            {{ c.nom }} ({{ c.anneeScolaire }})
          </li>
        </ul>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from "vue";
import { api } from "../services/api";
const ecoles = ref([]);
const classes = ref([]);
const selectedEcoleId = ref(null);
async function loadEcoles() {
  const { data } = await api.get("/api/ecoles");
  ecoles.value = data;
  selectedEcoleId.value = ecoles.value[0]?.id ?? null;
}
async function loadClasses() {
  if (!selectedEcoleId.value) return;
  const { data } = await api.get(`/api/ecoles/${selectedEcoleId.value}`);
  classes.value = data?.classes || [];
}
</script>
