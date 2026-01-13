<template>
  <PrincipalDashboard v-if="auth.isPrincipal || auth.isAdmin" />
  <TeacherDashboard v-else-if="auth.isTeacher" />
  <v-alert v-else type="info" variant="tonal">Aucun rôle détecté.</v-alert>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useAuthStore } from "../stores/auth";
import PrincipalDashboard from "./PrincipalDashboard.vue";
import TeacherDashboard from "./TeacherDashboard.vue";
const auth = useAuthStore();

const loading = ref(false);
const error = ref(null);

onMounted(async () => {
  try {
    loading.value = true;
    await auth.whoAmI();
  } catch (e) {
    error.value = "Impossible de charger votre profil.";
  } finally {
    loading.value = false;
  }
});
</script>
