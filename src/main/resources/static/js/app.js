'use strict';

$(document).ready(() => {
    initializeButtons();
    navbarBurgerToggle();
});

const initializeButtons = () => {
    controlButtons();
    otherButtons();
    submitButtons();
};

const navbarBurgerToggle = () => {
    // Get all "navbar-burger" elements
    const $navbarBurgers = Array.prototype.slice.call(document.querySelectorAll('.navbar-burger'), 0);

    // Check if there are any navbar burgers
    if ($navbarBurgers.length > 0) {

        // Add a click event on each of them
        $navbarBurgers.forEach( el => {
            el.addEventListener('click', () => {

                // Get the target from the "data-target" attribute
                const target = el.dataset.target;
                const $target = document.getElementById(target);

                // Toggle the "is-active" class on both the "navbar-burger" and the "navbar-menu"
                el.classList.toggle('is-active');
                $target.classList.toggle('is-active');

            });
        });
    }
};


const controlButtons = () => {
    $(".create-tables").click(() => {
        $.ajax({
            url: "api/control/?operation=create",
            type: "POST",
            success: (response) => {
                window.console.log(response);
            },
            error: (jqXHR, status, error) => {
                window.console.log(status + error);
            }
        });
    });
    $(".drop-tables").click(() => {
        $.ajax({
            url: "api/control/?operation=drop",
            type: "POST",
            success: (response) => {
                window.console.log(response);
            },
            error: (jqXHR, status, error) => {
                window.console.log(status + error);
            }
        });          
    });
    $(".fill-tables").click(() => {
        $.ajax({
            url: "api/control/?operation=fill",
            type: "POST",
            success: (response) => {
                window.console.log(response);
            },
            error: (jqXHR, status, error) => {
                window.console.log(status + error);
            }
        });          
    });  
};

const hideAllButOne = (form) => {
    const toHide = `.main form:not(${form})`;
    const toShow = `.main form${form}`;
    
    $(toHide).hide();
    $(toShow).show();
};

const otherButtons = () => {
    $(".home").click(() => {hideAllButOne(".initial-prompt");});
    $(".new-account-button").click(() => {hideAllButOne(".new-account");});
    $(".close-account-button").click(() => {hideAllButOne(".close-account");});
    $(".buy-button").click(() => {hideAllButOne(".buy");});
    $(".pay-button").click(() => {hideAllButOne(".pay");});
    $(".return-button").click(() => {hideAllButOne(".return");});
    $(".questions-button").click(() => {hideAllButOne(".questions");});
    $(".gu-button").click(() => {hideAllButOne(".gu");});
    $(".bu-button").click(() => {hideAllButOne(".bu");});
    $(".mom-button").click(() => {hideAllButOne(".mom");});
};

const submitButtons = () => {
    $('.new-account').submit(function(e){
        e.preventDefault();
        $.ajax({
            url: `/api/new-account?${$('.new-account').serialize()}`,
            type: 'get',
            success: function(response){
                $(".modal").addClass("is-active");
                $(".modal-text").text(response);
            },
            error: (jqXHR, status, error) => {
                window.console.log(status + error);
            }
        });
    });
    $('.close-account').submit(function(e){
        e.preventDefault();
        $.ajax({
            url: `/api/close-account?${$('.close-account').serialize()}`,
            type: 'get',
            success: function(response){
                $(".modal").addClass("is-active");
                $(".modal-text").text(response);
            },
            error: (jqXHR, status, error) => {
                window.console.log(status + error);
            }
        });
    });
    $('.gu').submit(function(e){
        e.preventDefault();
        $.ajax({
            url: `/api/good-users`,
            type: 'get',
            success: function(response){
                $(".good-users").html(response);
            },
            error: (jqXHR, status, error) => {
                window.console.log(status + error);
            }
        });
    });
    $('.bu').submit(function(e){
        e.preventDefault();
        $.ajax({
            url: `/api/bad-users`,
            type: 'get',
            success: function(response){
                $(".bad-users").html(response);
            },
            error: (jqXHR, status, error) => {
                window.console.log(status + error);
            }
        });
    });
    $('.pay').submit(function(e){
        e.preventDefault();
        $.ajax({
            url: `/api/pay?${$('.pay').serialize()}`,
            type: 'get',
            success: function(response){
                $(".modal").addClass("is-active");
                $(".modal-text").text(response);
            },
            error: (jqXHR, status, error) => {
                window.console.log(status + error);
            }
        });
    });
    $('.return').submit(function(e){
        e.preventDefault();
        $.ajax({
            url: `/api/return?${$('.return').serialize()}`,
            type: 'get',
            success: function(response){
                $(".modal").addClass("is-active");
                $(".modal-text").text(response);
            },
            error: (jqXHR, status, error) => {
                window.console.log(status + error);
            }
        });
    });
    $('.buy').submit(function(e){
        e.preventDefault();
        $.ajax({
            url: `/api/buy?${$('.buy').serialize()}`,
            type: 'get',
            success: function(response){
                $(".modal").addClass("is-active");
                $(".modal-text").text(response);
            },
            error: (jqXHR, status, error) => {
                window.console.log(status + error);
            }
        });
    });
    $('.mom').submit(function(e){
        e.preventDefault();
        $.ajax({
            url: `/api/merchant-of-the-month`,
            type: 'get',
            success: function(response){
                $(".motm").html(response);
            },
            error: (jqXHR, status, error) => {
                window.console.log(status + error);
            }
        });
    });
    $('.question-1').submit(function(e) {
        e.preventDefault();
        $.ajax({
            url: `/api/question-1?${$('.question-1').serialize()}`,
            type: 'get',
            success: function(response){
                $(".question-1-content").html(response);
            },
            error: (jqXHR, status, error) => {
                window.console.log(status + error);
            }
        });
    });
    $('.question-2').submit(function(e) {
        e.preventDefault();
        $.ajax({
            url: `/api/question-2?${$('.question-2').serialize()}`,
            type: 'get',
            success: function(response){
                $(".question-2-content").html(response);
            },
            error: (jqXHR, status, error) => {
                window.console.log(status + error);
            }
        });
    });
    $('.question-3').submit(function(e) {
        e.preventDefault();
        $.ajax({
            url: `/api/question-3?${$('.question-3').serialize()}`,
            type: 'get',
            success: function(response){
                $(".question-3-content").html(response);
            },
            error: (jqXHR, status, error) => {
                window.console.log(status + error);
            }
        });
    });
};

document.addEventListener('DOMContentLoaded', () => {
    // Functions to open and close a modal
    function openModal($el) {
        $el.classList.add('is-active');
    }

    function closeModal($el) {
        $el.classList.remove('is-active');
    }

    function closeAllModals() {
        (document.querySelectorAll('.modal') || []).forEach(($modal) => {
            closeModal($modal);
        });
    }

    // Add a click event on buttons to open a specific modal
    (document.querySelectorAll('.js-modal-trigger') || []).forEach(($trigger) => {
        const modal = $trigger.dataset.target;
        const $target = document.getElementById(modal);
        console.log($target);

        $trigger.addEventListener('click', () => {
            openModal($target);
        });
    });

    // Add a click event on various child elements to close the parent modal
    (document.querySelectorAll('.modal-background, .modal-close, .modal-card-head .delete, .modal-card-foot .button') || []).forEach(($close) => {
        const $target = $close.closest('.modal');

        $close.addEventListener('click', () => {
            closeModal($target);
        });
    });

    // Add a keyboard event to close all modals
    document.addEventListener('keydown', (event) => {
        const e = event || window.event;

        if (e.keyCode === 27) { // Escape key
            closeAllModals();
        }
    });
});
