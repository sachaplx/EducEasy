<template>
  <v-app-bar app color="primary" flat class="nav-appbar">
    <div class="nav-grid">
      <div class="nav-left">
        <RouterLink to="/" class="nav-link">Accueil</RouterLink>
        <RouterLink v-if="!auth.isAuthenticated" to="/login" class="nav-link"
          >Se connecter</RouterLink
        >
        <RouterLink v-if="!auth.isAuthenticated" to="/register" class="nav-link"
          >Créer un compte</RouterLink
        >
      </div>

      <div class="nav-center">
        <RouterLink to="/" class="nav-link-title">Educ'Easy</RouterLink>
      </div>

      <div class="nav-right">
        <SearchBar
          v-if="auth.isAuthenticated"
          class="nav-search"
          @select="goToEleve"
        />
        <v-chip
          v-if="auth.isPrincipal"
          density="comfortable"
          variant="text"
          color="white"
          >Directeur</v-chip
        >
        <v-chip
          v-else-if="auth.isTeacher"
          density="comfortable"
          variant="text"
          color="white"
          >Instituteur</v-chip
        >
        <v-btn
          v-if="auth.isAuthenticated"
          variant="tonal"
          color="white"
          @click="onLogout"
          >Logout</v-btn
        >
      </div>
    </div>
  </v-app-bar>
</template>

<script setup>
import { useRouter, RouterLink } from "vue-router";
import { useAuthStore } from "../stores/auth";
import SearchBar from "./SearchBar.vue";

const router = useRouter();
const auth = useAuthStore();

function onLogout() {
  auth.logout();
  router.push({ name: "login" });
}
function goToEleve(pupil) {
  router.push({ name: "pupil-profile", params: { id: pupil.id } });
}
</script>

<style scoped>
.nav-link,
.nav-link:link,
.nav-link:visited,
.nav-link:hover,
.nav-link:active,
.nav-link-title,
.nav-link-title:link,
.nav-link-title:visited,
.nav-link-title:hover,
.nav-link-title:active {
  text-decoration: none !important;
  border-bottom: 0 !important;
}

/* évite la “ligne” fantôme liée au line-height */
.nav-link {
  display: inline-flex;
  align-items: center;
  line-height: 1; /* <- important */
  padding: 0 10px;
  height: 40px; /* aligne visuellement dans la barre */
  color: white; /* si ta barre est bleue */
  opacity: 0.9;
}

.nav-link-title {
  font-weight: 700;
  font-size: 1.25rem;
  letter-spacing: 0.2px;
  color: white; /* si ta barre est bleue */
  opacity: 0.9;
}

/* état actif : underline désactivé, on peut mettre une légère opacité */
.nav-link:hover {
  opacity: 1;
}

.nav-grid {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding-inline: 16px;
}
.nav-left {
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: flex-start;
}
.nav-center {
  display: flex;
  align-items: center;
  justify-content: center;
}
.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: flex-end;
}

.brand {
  font-weight: 700;
  letter-spacing: 0.2px;
}
.link {
  opacity: 0.9;
}

.nav-search {
  min-width: 320px;
  max-width: 42vw;
}

.nav-appbar {
  box-shadow: 0 5px 15px -5px rgba(0, 0, 0, 0.6) !important;
}

@media (max-width: 960px) {
  .nav-search {
    min-width: 220px;
    max-width: 60vw;
  }
}
@media (max-width: 600px) {
  .nav-grid {
    grid-template-columns: 1fr auto;
    grid-auto-rows: auto;
    row-gap: 8px;
  }
  .nav-center {
    grid-column: 1 / -1;
    order: -1;
  }
}
</style>
