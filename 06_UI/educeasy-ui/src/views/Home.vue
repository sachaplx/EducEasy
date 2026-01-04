<template>
  <PrincipalDashboard v-if="auth.isPrincipal" />
  <TeacherDashboard v-else-if="auth.isTeacher" />
  <v-alert v-else type="info" variant="tonal">Aucun rôle détecté.</v-alert>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { api } from "../services/api";
import { useAuthStore } from "../stores/auth";
import PrincipalDashboard from "./PrincipalDashboard.vue";
import TeacherDashboard from "./TeacherDashboard.vue";
const auth = useAuthStore();

const loading = ref(false);
const error = ref(null);
const me = ref(null);

const displayName = computed(() => {
  if (me.value?.firstName) return me.value.firstName;
  if (me.value?.lastName) return me.value.lastName;
  return "";
});

onMounted(async () => {
  try {
    loading.value = true;
    const { data } = await api.get("/whoami");
    me.value = data;
  } catch (e) {
    error.value = "Impossible de charger votre profil.";
  } finally {
    loading.value = false;
  }
});
</script>
