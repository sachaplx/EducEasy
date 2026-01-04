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
          <div class="d-flex justify-end mb-4">
            <v-btn
              variant="text"
              size="small"
              class="text-caption px-0"
              :disabled="loading"
              @click="onForgotPassword"
            >
              Mot de passe oublié ?
            </v-btn>
          </div>
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
          <v-alert
            v-if="errorMessage"
            type="error"
            variant="tonal"
            class="mt-4"
            density="compact"
          >
            {{ errorMessage }}
          </v-alert>
          <v-alert
            v-if="infoMessage"
            type="info"
            variant="tonal"
            class="mt-4"
            density="compact"
          >
            {{ infoMessage }}
          </v-alert>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import axios from "axios";
import { onMounted, ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useAuthStore } from "../stores/auth";

const auth = useAuthStore();
const router = useRouter();
const route = useRoute();

const username = ref("");
const password = ref("");
const loading = ref(false);
const showPassword = ref(false);
const form = ref();
const errorMessage = ref("");
const infoMessage = ref("");

const rules = {
  required: (v) => !!v || "Ce champ est requis",
};

onMounted(() => {
  if (route.query.reset === "ok") {
    infoMessage.value =
      "Votre mot de passe a été réinitialisé avec succès. Vous pouvez maintenant vous connecter.";
  }
});

function onRegister() {
  router.push("register");
}

async function submit() {
  loading.value = true;
  try {
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

    if (s === 403 || s === 400) {
      errorMessage.value =
        "Identifiants invalides ou compte non confirmé. Veuillez vérifier vos informations ou confirmer votre adresse e-mail.";
      return;
    }

    if (axios.isCancel && axios.isCancel(e)) {
      return;
    }

    const m = e?.response?.data?.error || e?.message;
    alert(`Échec connexion (${s ?? "network"}): ${m ?? "invalid credentials"}`);
  } finally {
    loading.value = false;
  }
}

function onForgotPassword() {
  router.push({ name: "forgot-password" });
}
</script>

<style scoped>
@media (min-width: 960px) {
  .login-view {
    height: 100vh;
    overflow: hidden;
  }
}

.no-grow-details :deep(.v-input__details) {
  min-height: 22px;
}
</style>

<style>
@media (min-width: 960px) {
  html,
  body {
    overflow: hidden;
  }
}
</style>
