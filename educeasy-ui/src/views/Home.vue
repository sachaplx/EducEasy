<template>
  <div class="container">
    <h2>Bienvenue, {{ auth.username || "Utilisateur" }}</h2>
    <p style="color: #6b7280">Vue d'accueil en fonction du rôle.</p>
    <PrincipalDashboard v-if="auth.isDirecteur" />
    <TeacherDashboard v-else-if="auth.isInstituteur" />
    <div v-else class="card">Aucun rôle détecté.</div>
  </div>
</template>

<script setup>
import { useAuthStore } from "../stores/auth";
import PrincipalDashboard from "./PrincipalDashboard.vue";
import TeacherDashboard from "./TeacherDashboard.vue";
const auth = useAuthStore();
</script>
