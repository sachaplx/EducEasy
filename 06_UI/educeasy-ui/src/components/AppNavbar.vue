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
        <span class="ee-brand__text">EDUC'EASY</span>
      </RouterLink>

      <div class="ee-search-wrap d-none d-md-flex">
        <SearchBar
          v-if="auth.isAuthenticated"
          input-class="ee-search-input"
          @select="goToEleve"
          @quick-note="handleQuickNote"
          @quick-absence="handleQuickAbsence"
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
          <!-- Search button for mobile -->
          <v-btn
            icon
            variant="text"
            class="ee-icon-btn d-flex d-md-none"
            aria-label="Rechercher"
            @click="searchDialog = true"
          >
            <v-icon size="22">mdi-magnify</v-icon>
          </v-btn>

          <!-- Notifications - TODO: À activer quand le backend sera prêt -->
          <!--
          <v-menu
            v-model="notificationsMenu"
            location="bottom end"
            offset="10"
            max-width="420"
            :close-on-content-click="false"
          >
            <template #activator="{ props }">
              <v-badge
                :content="notifications.unreadCount"
                :model-value="notifications.unreadCount > 0"
                color="error"
                floating
                offset-x="-2"
                offset-y="-2"
              >
                <v-btn
                  v-bind="props"
                  icon
                  variant="text"
                  class="ee-icon-btn"
                  aria-label="Notifications"
                >
                  <v-icon size="22">mdi-bell-outline</v-icon>
                </v-btn>
              </v-badge>
            </template>

            <v-card rounded="lg">
              <v-card-title class="d-flex align-center pa-3" style="gap: 8px">
                <v-icon>mdi-bell</v-icon>
                <span class="text-h6">Notifications</span>
                <v-spacer />
                <v-btn
                  v-if="notifications.unreadCount > 0"
                  variant="text"
                  size="small"
                  @click="notifications.markAllAsRead()"
                >
                  Tout marquer lu
                </v-btn>
              </v-card-title>
              <v-divider />

              <div v-if="notifications.loading" class="pa-4 text-center">
                <v-progress-circular indeterminate size="32" />
              </div>

              <div
                v-else-if="!notifications.notifications.length"
                class="pa-8 text-center text-medium-emphasis"
              >
                <v-icon size="48" color="grey-lighten-1"
                  >mdi-bell-off-outline</v-icon
                >
                <div class="mt-2">Aucune notification</div>
              </div>

              <v-list
                v-else
                density="compact"
                class="py-0"
                max-height="400"
                style="overflow-y: auto"
              >
                <v-list-item
                  v-for="notif in notifications.sortedNotifications"
                  :key="notif.id"
                  :class="{ 'bg-blue-lighten-5': !notif.read }"
                  class="notification-item"
                  @click="notifications.markAsRead(notif.id)"
                >
                  <template #prepend>
                    <v-avatar :color="notif.color" size="36">
                      <v-icon :icon="notif.icon" size="20" color="white" />
                    </v-avatar>
                  </template>

                  <v-list-item-title class="text-body-2 font-weight-medium">
                    {{ notif.title }}
                  </v-list-item-title>
                  <v-list-item-subtitle class="text-caption">
                    {{ notif.message }}
                  </v-list-item-subtitle>
                  <v-list-item-subtitle
                    class="text-caption text-medium-emphasis mt-1"
                  >
                    {{ formatNotificationTime(notif.createdAt) }}
                  </v-list-item-subtitle>

                  <template #append>
                    <div class="d-flex align-center" style="gap: 4px">
                      <v-icon v-if="!notif.read" size="8" color="primary">
                        mdi-circle
                      </v-icon>
                      <v-btn
                        icon
                        size="x-small"
                        variant="text"
                        @click.stop="notifications.removeNotification(notif.id)"
                        aria-label="Supprimer"
                      >
                        <v-icon size="16">mdi-close</v-icon>
                      </v-btn>
                    </div>
                  </template>
                </v-list-item>
              </v-list>

              <v-divider v-if="notifications.notifications.length" />
              <v-card-actions v-if="notifications.notifications.length">
                <v-btn
                  variant="text"
                  size="small"
                  block
                  @click="notifications.clearAll()"
                >
                  Tout effacer
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-menu>
          -->

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

                <v-list-item class="text-red" @click="confirmLogout">
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

    <!-- Mobile Search Dialog -->
    <v-dialog v-model="searchDialog" max-width="600">
      <v-card rounded="lg">
        <v-card-title class="d-flex align-center pa-4" style="gap: 8px">
          <v-icon>mdi-magnify</v-icon>
          <span>Rechercher un élève</span>
          <v-spacer />
          <v-btn
            icon
            size="small"
            variant="text"
            @click="searchDialog = false"
            aria-label="Fermer"
          >
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text class="pa-4 pt-0">
          <SearchBar
            v-if="auth.isAuthenticated"
            theme="light"
            @select="handleMobileSearch"
          />
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- Quick Note Dialog -->
    <AddNoteDialog
      v-if="selectedPupil"
      v-model="quickNoteDialog"
      :pupil-id="selectedPupil.id"
    />

    <!-- Quick Absence Dialog -->
    <QuickAbsenceDialog
      v-if="selectedPupil"
      v-model="quickAbsenceDialog"
      :pupils="[selectedPupil]"
    />

    <!-- Logout Confirmation Dialog -->
    <ConfirmDialog
      v-model="logoutDialog"
      type="warning"
      title="Confirmer la déconnexion"
      message="Êtes-vous sûr de vouloir vous déconnecter ?"
      confirm-text="Se déconnecter"
      confirm-icon="mdi-logout"
      @confirm="onLogout"
    />
  </v-app-bar>
</template>

<script setup>
import { ref, computed, watch, onMounted } from "vue";
import { useRouter, RouterLink } from "vue-router";
import { useAuthStore } from "../stores/auth";
// import { useNotificationStore } from "../stores/notification"; // TODO: À activer quand le backend sera prêt
import SearchBar from "./SearchBar.vue";
import AddNoteDialog from "./dialogs/AddNoteDialog.vue";
import QuickAbsenceDialog from "./dialogs/QuickAbsenceDialog.vue";
import ConfirmDialog from "./dialogs/ConfirmDialog.vue";

const router = useRouter();
const auth = useAuthStore();
// const notifications = useNotificationStore(); // TODO: À activer quand le backend sera prêt

const searchDialog = ref(false);
const quickNoteDialog = ref(false);
const quickAbsenceDialog = ref(false);
const selectedPupil = ref(null);
const logoutDialog = ref(false);
// const notificationsMenu = ref(false); // TODO: À activer quand le backend sera prêt

// TODO: À activer quand le backend sera prêt
// onMounted(() => {
//   if (auth.isAuthenticated) {
//     notifications.fetchNotifications();
//   }
// });

// Charger les infos utilisateur quand authentifié
watch(
  () => auth.isAuthenticated,
  (isAuth) => {
    if (isAuth && !auth.me) {
      auth.whoAmI();
    }
  },
  { immediate: true },
);

const displayName = computed(() => {
  if (auth.me?.firstName) return auth.me.firstName;
  if (auth.me?.lastName) return auth.me.lastName;
  if (auth.username) return auth.username;
  return "";
});

const userInitials = computed(() => {
  const m = auth.me;
  if (!m) return "U";
  const fn = (m.firstName || "").trim().charAt(0).toUpperCase();
  const ln = (m.lastName || "").trim().charAt(0).toUpperCase();
  return fn + ln || "U";
});

const roleLabel = computed(() => {
  if (auth.isPrincipal) return "Directeur";
  if (auth.isTeacher) return "Instituteur";
  return "Utilisateur";
});

function confirmLogout() {
  logoutDialog.value = true;
}

function onLogout() {
  logoutDialog.value = false;
  auth.logout();
  router.push({ name: "login" });
}

function goToEleve(pupil) {
  router.push({ name: "pupil-profile", params: { id: pupil.id } });
}

function handleMobileSearch(pupil) {
  searchDialog.value = false;
  goToEleve(pupil);
}

function handleQuickNote(pupil) {
  selectedPupil.value = pupil;
  quickNoteDialog.value = true;
}

function handleQuickAbsence(pupil) {
  selectedPupil.value = pupil;
  quickAbsenceDialog.value = true;
}

function goToProfile() {
  router.push({ name: "account" });
}

// TODO: À activer quand le backend sera prêt
// function formatNotificationTime(dateString) {
//   const date = new Date(dateString);
//   const now = new Date();
//   const diffMs = now - date;
//   const diffMins = Math.floor(diffMs / 60000);
//   const diffHours = Math.floor(diffMs / 3600000);
//   const diffDays = Math.floor(diffMs / 86400000);

//   if (diffMins < 1) return "À l'instant";
//   if (diffMins < 60) return `Il y a ${diffMins} min`;
//   if (diffHours < 24) return `Il y a ${diffHours}h`;
//   if (diffDays === 1) return "Hier";
//   if (diffDays < 7) return `Il y a ${diffDays} jours`;

//   return date.toLocaleDateString("fr-FR", {
//     day: "numeric",
//     month: "short",
//   });
// }
</script>

<style scoped>
.ee-header {
  position: sticky;
  top: 0;
  z-index: 50;
}

.ee-header :deep(.v-toolbar),
.ee-header :deep(.v-toolbar__content) {
  background: linear-gradient(90deg, #2563eb, #3b82f6) !important;
}

.ee-header :deep(.v-toolbar) {
  box-shadow: 0 10px 22px rgba(0, 0, 0, 0.12) !important;
}

.ee-header__row {
  width: 100%;
  height: 64px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

@media (min-width: 960px) {
  .ee-header__row {
    padding: 0 24px;
    gap: 16px;
  }
}

.ee-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  color: rgba(255, 255, 255, 0.95);
  text-decoration: none;
  flex-shrink: 0;
}

@media (max-width: 600px) {
  .ee-brand__text {
    display: none;
  }
}

.ee-logo-box {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
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
  white-space: nowrap;
}

.ee-search-wrap {
  flex: 1;
  max-width: 520px;
  margin: 0 12px;
}

@media (min-width: 960px) {
  .ee-search-wrap {
    margin: 0 24px;
  }
}

.ee-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: flex-end;
  flex-shrink: 0;
}

.ee-icon-btn {
  color: rgba(255, 255, 255, 0.95);
}
.ee-icon-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.ee-profile-btn {
  color: rgba(255, 255, 255, 0.95) !important;
  padding: 6px 8px !important;
  border-radius: 10px !important;
  height: auto !important;
  min-width: auto !important;
}
.ee-profile-btn:hover {
  background: rgba(255, 255, 255, 0.1) !important;
}

.ee-profile-btn :deep(.v-btn__content) {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ee-avatar {
  border: 2px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.2);
  flex-shrink: 0;
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
  line-height: 1.2;
  min-width: 0;
}
.ee-profile-name {
  font-size: 13px;
  font-weight: 700;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 120px;
}
.ee-profile-role {
  font-size: 11px;
  opacity: 0.75;
  white-space: nowrap;
}

.ee-link {
  color: rgba(255, 255, 255, 0.92);
  text-decoration: none;
  opacity: 0.9;
  padding: 8px 12px;
  border-radius: 10px;
  white-space: nowrap;
  transition: all 0.2s;
}
.ee-link:hover {
  opacity: 1;
  background: rgba(255, 255, 255, 0.1);
}

@media (max-width: 600px) {
  .ee-link {
    padding: 6px 10px;
    font-size: 14px;
  }
}

/* Notifications */
.notification-item {
  cursor: pointer;
  transition: background-color 0.2s;
}

.notification-item:hover {
  background-color: rgba(0, 0, 0, 0.04) !important;
}
</style>
