<template>
  <v-container
    class="d-flex align-center justify-center login-view"
    style="
      min-height: calc(100vh - 64px);
      max-width: 100%;
      padding-bottom: 64px;
    "
  >
    <v-card style="max-width: 560px; width: 100%">
      <v-card-title class="py-2">Connexion</v-card-title>
      <v-card-text>
        <v-form ref="form" @submit.prevent="submit">
          <v-text-field
            v-model="username"
            label="Email ou nom d'utilisateur"
            :rules="[rules.required]"
            variant="outlined"
            :disabled="loading"
            density="default"
            class="mb-7 no-grow-details"
            hide-details="false"
          />
          <v-text-field
            v-model="password"
            :type="showPassword ? 'text' : 'password'"
            label="Mot de passe"
            variant="outlined"
            :rules="[rules.required]"
            :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
            @click:append-inner="showPassword = !showPassword"
            :disabled="loading"
            density="default"
            class="mb-7 no-grow-details"
            hide-details="false"
          />
          <v-btn
            type="submit"
            color="primary"
            :loading="loading"
            :disabled="loading"
            block
            height="48"
          >
            {{ loading ? "Connexion..." : "Se connecter" }}
          </v-btn>
        </v-form>
      </v-card-text>
    </v-card>
    <!-- Fixed bottom register footer -->
    <v-footer
      app
      elevation="0"
      color="primary"
      class="pa-4 d-flex justify-center"
      style="position: fixed; left: 0; right: 0; bottom: 0; height: 64px"
    >
      <div style="max-width: 520px; width: 100%">
        <v-btn
          color="white"
          variant="flat"
          @click="onRegister()"
          block
          height="44"
        >
          Créer un compte
        </v-btn>
      </div>
    </v-footer>
  </v-container>
</template>

<style scoped>
/* Disable scrolling on desktop for this view and keep content within viewport */
@media (min-width: 960px) {
  .login-view {
    height: 100vh;
    overflow: hidden;
  }
}

/* Keep v-text-field height stable when details (errors/hints) appear */
.no-grow-details :deep(.v-input__details) {
  min-height: 22px; /* reserve one-line details space to avoid layout shift */
}
</style>

<!-- Global styles to force no-scroll on desktop specifically for the login view -->
<style>
@media (min-width: 960px) {
  /* Disable page scroll on desktop while on the login view */
  html,
  body {
    overflow: hidden;
  }
}
</style>

<script setup>
import { ref } from "vue";
import { useRouter, useRoute, RouterLink } from "vue-router";
import { useAuthStore } from "../stores/auth";

const auth = useAuthStore();
const router = useRouter();
const route = useRoute();

const username = ref("");
const password = ref("");
const loading = ref(false);
const showPassword = ref(false);
const form = ref();

const rules = {
  required: (v) => !!v || "Ce champ est requis",
};

function onRegister() {
  router.push("register");
}

async function submit() {
  loading.value = true;
  try {
    // Validate form first to show required error messages
    const valid = await form.value?.validate();
    if (!valid) {
      return;
    }
    await auth.login({
      username: username.value,
      password: password.value,
    });
    router.push(route.query.redirect || "/");
  } catch (e) {
    const s = e?.response?.status;
    const m = e?.response?.data?.error || e?.message;
    alert(`Échec connexion (${s ?? "network"}): ${m ?? "invalid credentials"}`);
  } finally {
    loading.value = false;
  }
}
</script>
