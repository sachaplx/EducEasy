<template>
  <header class="navbar">
    <div class="container" navbar-inner>
      <div class="nav-links">
        <strong>Educ'Easy</strong>
        <router-link to="/">Accueil</router-link>
        <router-link v-if="!auth.isAuthenticated" to="/login" class="badge"
          >Login</router-link
        >
        <router-link v-if="!auth.isAuthenticated" to="/register" class="badge"
          >Register</router-link
        >
      </div>
      <SearchBar v-if="auth.isAuthenticated" @select="goToEleve" />
      <div class="nav-right">
        <span v-if="auth.isDirecteur" class="badge">Directeur</span>
        <span v-else-if="auth.isInstituteur" class="badge">Instituteur</span>
        <button
          v-if="auth.isAuthenticated"
          class="btn secondary"
          @click="onLogout"
        >
          Logout
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";
import SearchBar from "./SearchBar.vue";
const router = useRouter();
const auth = useAuthStore();
function onLogout() {
  auth.logout();
  router.push({ name: "login" });
}
function goToEleve(eleve) {
  alert(`Élève: ${eleve.prenom} ${eleve.nom} (id=${eleve.id})`);
}
</script>
