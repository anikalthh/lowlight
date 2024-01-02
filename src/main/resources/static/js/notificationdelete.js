document.addEventListener('DOMContentLoaded', () => {
    (document.querySelectorAll('.notification .postalert') || []).forEach(($delete) => {
      const $notification = $delete.parentNode;
  
      $delete.addEventListener('click', () => {
        $notification.parentNode.removeChild($notification);
      });
    });
  });