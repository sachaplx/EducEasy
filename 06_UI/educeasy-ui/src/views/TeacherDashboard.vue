<template>
  <v-row dense>
    <v-col cols="12" md="6">
      <v-card>
        <v-card-title>Ma/mes classe(s)</v-card-title>
        <v-card-text>
          <div class="d-flex" style="gap: 8px">
            <v-text-field
              v-model.number="classeId"
              label="ID classe"
              variant="outlined"
              hide-details="auto"
              style="max-width: 180px"
            />
            <v-btn color="primary" @click="loadEleves">Charger élèves</v-btn>
          </div>
          <v-list class="mt-2">
            <v-list-item
              v-for="e in eleves"
              :key="e.id"
              :title="`${e.prenom} ${e.nom}`"
            />
          </v-list>
        </v-card-text>
      </v-card>
    </v-col>

    <v-col cols="12" md="6">
      <v-card>
        <v-card-title>Infos</v-card-title>
        <v-card-text class="text-medium-emphasis">
          Ajoute ici un récap (absences du jour, remarques récentes, etc.).
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
</template>

<script setup>
import { ref } from "vue";
import { api } from "../services/api";

const classeId = ref(1);
const eleves = ref([]);

async function loadEleves() {
  if (!classeId.value) return;
  const { data } = await api.get(`/classrooms/${classeId.value}/list/pupils`);
  eleves.value = data;
}
</script>
