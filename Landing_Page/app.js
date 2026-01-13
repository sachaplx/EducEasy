// Year
document.getElementById("year").textContent = new Date().getFullYear();

// Mobile menu toggle
const burger = document.querySelector(".burger");
const mobileMenu = document.querySelector(".mobileMenu");

if (burger && mobileMenu) {
  burger.addEventListener("click", () => {
    const isOpen = burger.getAttribute("aria-expanded") === "true";
    burger.setAttribute("aria-expanded", String(!isOpen));
    mobileMenu.hidden = isOpen;

    // close when clicking a link
    if (!isOpen) {
      mobileMenu.querySelectorAll("a").forEach(a => {
        a.addEventListener("click", () => {
          burger.setAttribute("aria-expanded", "false");
          mobileMenu.hidden = true;
        }, { once: true });
      });
    }
  });
}

// Smooth scroll for internal anchors
document.querySelectorAll('a[href^="#"]').forEach((a) => {
  a.addEventListener("click", (e) => {
    const id = a.getAttribute("href");
    if (!id || id === "#") return;
    const el = document.querySelector(id);
    if (!el) return;

    e.preventDefault();
    el.scrollIntoView({ behavior: "smooth", block: "start" });
  });
});

// Reveal on scroll
const revealEls = Array.from(document.querySelectorAll(".reveal"));

const io = new IntersectionObserver(
  (entries) => {
    entries.forEach((entry) => {
      if (!entry.isIntersecting) return;

      const el = entry.target;
      const delay = Number(el.dataset.delay || 0);
      el.style.transitionDelay = `${delay}ms`;
      el.classList.add("is-visible");
      io.unobserve(el);
    });
  },
  { threshold: 0.12 }
);

revealEls.forEach((el) => io.observe(el));
