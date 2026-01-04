<template>
  <v-container
    class="d-flex align-center justify-center confirm-view"
    style="
      min-height: calc(100vh - 64px);
      max-width: 100%;
      padding-bottom: 64px;
    "
  >
    <v-card style="max-width: 560px; width: 100%">
      <v-card-title class="py-3 text-h6"> Confirmation de compte </v-card-title>

      <v-divider />

      <v-card-text class="pt-6 pb-4">
        <div
          v-if="loading"
          class="d-flex flex-column align-center justify-center text-center"
        >
          <v-progress-circular indeterminate size="40" class="mb-4" />
          <div class="text-body-1 mb-1">Nous confirmons votre compte...</div>
          <div class="text-body-2 text-medium-emphasis">
            Merci de patienter quelques secondes.
          </div>
        </div>

        <div
          v-else-if="success"
          class="d-flex flex-column align-center justify-center text-center"
        >
          <v-icon size="56" color="success" class="mb-4">
            mdi-check-circle
          </v-icon>
          <div class="text-h6 mb-2">Votre compte est confirmé 🎉</div>
          <div class="text-body-2 text-medium-emphasis mb-6">
            Vous êtes maintenant connecté·e. Vous allez être redirigé·e.
          </div>

          <v-btn color="primary" variant="flat" height="44" @click="goToHome">
            Accéder à mon espace
          </v-btn>
        </div>

        <div
          v-else
          class="d-flex flex-column align-center justify-center text-center"
        >
          <v-icon size="56" color="error" class="mb-4">
            mdi-alert-circle
          </v-icon>
          <div class="text-h6 mb-2">Impossible de confirmer votre compte</div>
          <div class="text-body-2 text-medium-emphasis mb-4">
            {{ errorMessage }}
          </div>

          <v-btn color="primary" variant="flat" height="44" @click="goToLogin">
            Retour à la connexion
          </v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();

const loading = ref(true);
const success = ref(false);
const errorMessage = ref("");

function goToLogin() {
  router.push("/login");
}

function goToHome() {
  router.push("/");
}

onMounted(async () => {
  const token = route.query.token;

  if (!token || typeof token !== "string") {
    loading.value = false;
    success.value = false;
    errorMessage.value = "Token manquant dans l'URL.";
    return;
  }

  try {
    await auth.confirmFromEmail(token);
    success.value = true;
    loading.value = false;

    // Redirection douce après 1.5s
    setTimeout(() => {
      goToHome();
    }, 1500);
  } catch (e) {
    loading.value = false;
    success.value = false;
    const s = e?.response?.status;
    const msg =
      e?.response?.data?.error ||
      e?.message ||
      "Une erreur inattendue est survenue.";
    errorMessage.value = `(${s ?? "network"}) ${msg}`;
  }
});
</script>

<style scoped>
@media (min-width: 960px) {
  .confirm-view {
    height: 100vh;
    overflow: hidden;
  }
}
</style>
