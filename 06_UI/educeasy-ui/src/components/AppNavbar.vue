<template>
  <v-app-bar
    app
    flat
    color="transparent"
    :elevation="0"
    height="64"
    class="ee-header"
  >
    <div class="ee-header__row">
      <RouterLink to="/" class="ee-brand">
        <div class="ee-logo-box">
          <span class="ee-logo-letter">E</span>
        </div>
        <span class="ee-brand__text">Educ'Easy</span>
      </RouterLink>

      <!-- Search (md+) -->
      <div class="ee-search-wrap d-none d-md-flex">
        <SearchBar
          v-if="auth.isAuthenticated"
          input-class="ee-search-input"
          @select="goToEleve"
        />
      </div>

      <div class="ee-actions">
        <template v-if="!auth.isAuthenticated">
          <RouterLink to="/login" class="ee-link">Se connecter</RouterLink>
          <RouterLink to="/register" class="ee-link"
            >Créer un compte</RouterLink
          >
        </template>

        <template v-else>
          <v-badge
            :content="notificationsCount"
            color="amber"
            floating
            bordered
          >
            <v-btn
              icon
              variant="text"
              class="ee-icon-btn"
              aria-label="Notifications"
              @click="goToNotifications"
            >
              <v-icon size="22">mdi-bell-outline</v-icon>
            </v-btn>
          </v-badge>

          <!-- Profile dropdown -->
          <v-menu location="bottom end" offset="10">
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                variant="text"
                class="ee-profile-btn"
                aria-label="Profil"
              >
                <v-avatar class="ee-avatar" size="32">
                  <span class="ee-avatar-text">{{ userInitials }}</span>
                </v-avatar>

                <div class="d-none d-md-flex ee-profile-text">
                  <div class="ee-profile-name">{{ displayName }}</div>
                  <div class="ee-profile-role">{{ roleLabel }}</div>
                </div>

                <v-icon class="d-none d-md-flex" size="18" style="opacity: 0.8">
                  mdi-menu-down
                </v-icon>
              </v-btn>
            </template>

            <v-card rounded="lg" min-width="220">
              <v-list density="comfortable">
                <v-list-item @click="goToProfile">
                  <template #prepend>
                    <v-icon size="18">mdi-account</v-icon>
                  </template>
                  <v-list-item-title>Mon profil</v-list-item-title>
                </v-list-item>

                <v-divider />

                <v-list-item class="text-red" @click="onLogout">
                  <template #prepend>
                    <v-icon size="18" color="red">mdi-logout</v-icon>
                  </template>
                  <v-list-item-title>Déconnexion</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-card>
          </v-menu>
        </template>
      </div>
    </div>
  </v-app-bar>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { api } from "../services/api";
import { useRouter, RouterLink } from "vue-router";
import { useAuthStore } from "../stores/auth";
import SearchBar from "./SearchBar.vue";

const router = useRouter();
const auth = useAuthStore();

const notificationsCount = 3; // TODO: brancher endpoint plus tard
const me = ref(null);
const loading = ref(false);
const error = ref(null);

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

const displayName = computed(() => {
  if (me.value?.firstName) return me.value.firstName;
  if (me.value?.lastName) return me.value.lastName;
  return "";
});

const userInitials = computed(() => {
  const u = auth?.user || {};
  const fn = (u.prenom || u.firstName || "").trim().charAt(0).toUpperCase();
  const ln = (u.nom || u.lastName || "").trim().charAt(0).toUpperCase();
  return fn + ln || "U";
});

const roleLabel = computed(() => {
  if (auth.isPrincipal) return "Directeur";
  if (auth.isTeacher) return "Instituteur";
  return "Utilisateur";
});

function onLogout() {
  auth.logout();
  router.push({ name: "login" });
}

function goToEleve(pupil) {
  router.push({ name: "pupil-profile", params: { id: pupil.id } });
}

function goToProfile() {
  router.push({ name: "account" });
}

function goToNotifications() {
  // TODO: adapte à ta route notifications
  // router.push({ name: "notifications" })
}
</script>

<style scoped>
/* Header gradient + shadow comme React */
.ee-header {
  position: sticky;
  top: 0;
  z-index: 50;
}

/* Vuetify met le background sur la toolbar interne => on force ici */
.ee-header :deep(.v-toolbar),
.ee-header :deep(.v-toolbar__content) {
  background: linear-gradient(90deg, #2563eb, #3b82f6) !important;
}

/* Optionnel: garder l'ombre comme la maquette */
.ee-header :deep(.v-toolbar) {
  box-shadow: 0 10px 22px rgba(0, 0, 0, 0.12) !important;
}

/* row layout */
.ee-header__row {
  width: 100%;
  height: 64px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

/* brand */
.ee-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  color: rgba(255, 255, 255, 0.95);
  text-decoration: none;
  min-width: 180px;
}
.ee-logo-box {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}
.ee-logo-letter {
  font-weight: 800;
  font-size: 18px;
  color: rgba(255, 255, 255, 0.95);
}
.ee-brand__text {
  font-weight: 800;
  font-size: 20px;
  letter-spacing: 0.2px;
}

/* search center */
.ee-search-wrap {
  flex: 1;
  max-width: 520px;
  margin: 0 24px;
}

/* actions */
.ee-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: flex-end;
  min-width: 220px;
}

/* icon button style */
.ee-icon-btn {
  color: rgba(255, 255, 255, 0.95);
}
.ee-icon-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

/* profile */
.ee-profile-btn {
  color: rgba(255, 255, 255, 0.95);
  padding: 6px 8px;
  border-radius: 10px;
}
.ee-profile-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.ee-avatar {
  border: 2px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.2);
}
.ee-avatar-text {
  font-weight: 800;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.95);
}

.ee-profile-text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-left: 8px;
  margin-right: 6px;
  line-height: 1.1;
}
.ee-profile-name {
  font-size: 13px;
  font-weight: 700;
}
.ee-profile-role {
  font-size: 11px;
  opacity: 0.75;
}

/* links when logged out */
.ee-link {
  color: rgba(255, 255, 255, 0.92);
  text-decoration: none;
  opacity: 0.9;
  padding: 6px 8px;
  border-radius: 10px;
}
.ee-link:hover {
  opacity: 1;
  background: rgba(255, 255, 255, 0.1);
}
</style>
