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
            class="log-button"
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
  const saved = localStorage.getItem("prefillEmail");
  if (saved) {
    username.value = saved;
    localStorage.removeItem("prefillEmail");
  }
  if (route.query.reset === "ok") {
    infoMessage.value =
      "Votre mot de passe a été réinitialisé avec succès. Vous pouvez maintenant vous connecter.";
  }
  if (route.query.register === "ok") {
    infoMessage.value =
      "Votre compte a été créé avec succès. Veuillez vérifier votre e-mail pour confirmer votre adresse avant de vous connecter.";
  }
});

function onRegister() {
  router.push("register");
}

async function submit() {
  loading.value = true;
  try {
    const res = await form.value?.validate?.();
    const isValid = typeof res === "boolean" ? res : res?.valid;
    if (!isValid) return;

    await auth.login({
      username: username.value,
      password: password.value,
    });

    // redirect safe (évite open-redirect)
    const q = route.query.redirect;
    const redirect = typeof q === "string" && q.startsWith("/") ? q : "/";

    router.push(redirect);
  } catch (e) {
    const s = e?.response?.status;

    if (s === 403 || s === 400) {
      errorMessage.value =
        "Identifiants invalides ou compte non confirmé. Veuillez vérifier vos informations ou confirmer votre adresse e-mail.";
      return;
    }

    if (axios.isCancel(e)) return;

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
    min-height: 100vh;
    overflow-y: auto;
    overflow-x: hidden;
  }
}

.no-grow-details :deep(.v-input__details) {
  min-height: 22px;
}

.log-button {
  background: linear-gradient(90deg, #2563eb, #3b82f6) !important;
  color: white !important;
}
</style>
